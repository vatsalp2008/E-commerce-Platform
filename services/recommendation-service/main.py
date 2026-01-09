import pandas as pd
from fastapi import FastAPI, HTTPException
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from typing import List, Optional
import requests
import os
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="Recommendation Service", version="1.0.0")

# Configuration
PRODUCT_SERVICE_URL = os.getenv("PRODUCT_SERVICE_URL", "http://product-service:8081")

@app.get("/health")
def health_check():
    return {"status": "healthy"}

@app.get("/api/recommendations/{product_id}")
async def get_recommendations(product_id: str, limit: int = 5):
    """
    Returns similar products based on the given product_id using content-based filtering.
    """
    try:
        # 1. Fetch products from Product Service
        # In a real scenario, we might cache this or use a database
        response = requests.get(f"{PRODUCT_SERVICE_URL}/api/products")
        if response.status_code != 200:
            logger.error(f"Failed to fetch products: {response.status_code}")
            return mock_recommendations(product_id, limit)
            
        products = response.json()
        if not products:
            raise HTTPException(status_code=404, detail="No products found")

        df = pd.DataFrame(products)
        
        # Ensure the columns exist
        required_cols = ['id', 'name', 'description', 'brand']
        for col in required_cols:
            if col not in df.columns:
                df[col] = ""

        # 2. Preprocess text for TF-IDF
        df['content'] = df['name'] + " " + df['description'] + " " + df['brand']
        df['content'] = df['content'].fillna('')

        # 3. Vectorize content
        tfidf = TfidfVectorizer(stop_words='english')
        tfidf_matrix = tfidf.fit_transform(df['content'])

        # 4. Calculate Cosine Similarity
        cosine_sim = cosine_similarity(tfidf_matrix, tfidf_matrix)

        # 5. Get recommendations
        try:
            idx = df.index[df['id'] == product_id].tolist()[0]
        except IndexError:
            logger.warning(f"Product {product_id} not found in current list, returning trending mocks.")
            return mock_recommendations(product_id, limit)

        sim_scores = list(enumerate(cosine_sim[idx]))
        sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)
        sim_scores = sim_scores[1:limit+1] # Skip the product itself

        product_indices = [i[0] for i in sim_scores]
        recommendations = df.iloc[product_indices].to_dict('records')
        
        # Strip internal 'content' column
        for rec in recommendations:
            rec.pop('content', None)

        return recommendations

    except Exception as e:
        logger.error(f"Recommendation error: {str(e)}")
        return mock_recommendations(product_id, limit)

def mock_recommendations(product_id: str, limit: int):
    """Fallback mock recommendations if service is down or data is missing"""
    return [
        {"id": "mock-1", "name": "Recommended Product 1", "category": "Electronics", "price": 99.99},
        {"id": "mock-2", "name": "Recommended Product 2", "category": "Home", "price": 49.99},
    ][:limit]
