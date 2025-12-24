import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np

def recommend_collaborative(user_id, history_data):
    """
    Generate recommendations based on user history.
    history_data: list of dicts with userId, productId, action
    """
    if not history_data:
        return []

    # detailed weight mapping
    weights = {'view': 1, 'click': 2, 'add_to_cart': 3, 'purchase': 5}
    
    df = pd.DataFrame(history_data)
    df['weight'] = df['action'].map(weights)
    
    # Create User-Item Matrix
    user_item_matrix = df.pivot_table(index='userId', columns='productId', values='weight', aggfunc='sum').fillna(0)
    
    if user_id not in user_item_matrix.index:
        return []

    # Calculate Cosine Similarity between users
    user_similarity = cosine_similarity(user_item_matrix)
    user_sim_df = pd.DataFrame(user_similarity, index=user_item_matrix.index, columns=user_item_matrix.index)
    
    # Get similar users
    similar_users = user_sim_df[user_id].sort_values(ascending=False).index[1:] # exclude self
    
    recommendations = {}
    
    for sim_user in similar_users:
        # Get products bought/liked by similar user
        sim_user_products = user_item_matrix.loc[sim_user]
        sim_user_products = sim_user_products[sim_user_products > 0].index.tolist()
        
        # Filter out products already interacted with by current user
        current_user_products = user_item_matrix.loc[user_id]
        current_user_products = current_user_products[current_user_products > 0].index.tolist()
        
        new_products = [p for p in sim_user_products if p not in current_user_products]
        
        for product in new_products:
            if product not in recommendations:
                recommendations[product] = 0
            # Add score weighted by user similarity
            recommendations[product] += user_sim_df.loc[user_id, sim_user]
            
    # Sort by score
    sorted_recs = sorted(recommendations.items(), key=lambda x: x[1], reverse=True)
    
    return [rec[0] for rec in sorted_recs[:10]]
