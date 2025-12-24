from flask import Flask, request, jsonify
from pymongo import MongoClient
import pandas as pd
from collaborative_filtering import recommend_collaborative
# from content_filtering import recommend_content_based

app = Flask(__name__)

# MongoDB Connection
client = MongoClient('mongodb://localhost:27017/')
db = client['ecommerce_tracking']
behavior_collection = db['userbehaviors']

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})

@app.route('/recommend/personalized/<int:user_id>', methods=['GET'])
def personalized_recommendations(user_id):
    try:
        # Fetch user history from MongoDB
        history = list(behavior_collection.find({'userId': user_id}))
        
        if not history:
            return jsonify({'recommendations': [], 'reason': 'No history found'})
            
        recommendations = recommend_collaborative(user_id, history)
        return jsonify({'recommendations': recommendations})
    except Exception as e:
        print(f"Error: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/recommend/similar/<int:product_id>', methods=['GET'])
def similar_products(product_id):
    # Placeholder for content-based filtering
    # recommendations = recommend_content_based(product_id)
    return jsonify({'recommendations': []})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001)
