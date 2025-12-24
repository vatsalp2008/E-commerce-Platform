import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';
import { Product } from '../types';

const ProductDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [product, setProduct] = useState<Product | null>(null);
    const [recommendations, setRecommendations] = useState<Product[]>([]);
    const { user } = useAuth();
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const { data } = await api.get(`/products/${id}`);
                setProduct(data);

                // Track view
                if (user) {
                    await api.post('/tracking', {
                        productId: Number(id),
                        action: 'view'
                    });

                    // Fetch recommendations (using personalized for user or similar for product)
                    // For now, let's fetch personalized if user is logged in
                    const recRes = await api.get(`/recommendations/personalized/${user.id}`);
                    // Note: ML service might return product IDs. Ideally, we need to hydrate them.
                    // But if our ML service is proxied via backend, backend should hydrate.
                    // Wait, my backend implementation of /recommendations proxy isn't done yet! 
                    // I need to implement backend proxy or call ML service directly?
                    // The prompt said "Main backend will communicate with it via HTTP REST APIs".
                    // So I should call backend /api/recommendations which calls ML service.

                    // Assuming backend proxy returns full product objects or IDs then we fetch.
                    // Let's implement backend proxy later. For now, let's just log.
                }
            } catch (error) {
                console.error('Error fetching product', error);
            } finally {
                setLoading(false);
            }
        };

        fetchProduct();
    }, [id, user]);

    const addToCart = async () => {
        try {
            await api.post('/cart/add', { productId: Number(id), quantity: 1 });
            // track add to cart
            if (user) {
                await api.post('/tracking', { productId: Number(id), action: 'add_to_cart' });
            }
            alert('Added to cart!');
        } catch (error) {
            alert('Failed to add to cart, are you logged in?');
        }
    }

    if (loading) return <div>Loading...</div>;
    if (!product) return <div>Product not found</div>;

    return (
        <div className="max-w-2xl mx-auto py-16 px-4 sm:py-24 sm:px-6 lg:max-w-7xl lg:px-8">
            <div className="lg:grid lg:grid-cols-2 lg:gap-x-8 lg:items-start">
                {/* Image */}
                <div className="w-full aspect-w-1 aspect-h-1 bg-gray-200 rounded-lg overflow-hidden sm:aspect-w-2 sm:aspect-h-3">
                    <img
                        src={product.imageUrl || 'https://via.placeholder.com/600'}
                        alt={product.name}
                        className="w-full h-full object-center object-cover"
                    />
                </div>

                {/* Info */}
                <div className="mt-10 px-4 sm:px-0 sm:mt-16 lg:mt-0">
                    <h1 className="text-3xl font-extrabold tracking-tight text-gray-900">{product.name}</h1>
                    <div className="mt-3">
                        <h2 className="sr-only">Product information</h2>
                        <p className="text-3xl text-gray-900">${product.price}</p>
                    </div>
                    <div className="mt-6">
                        <h3 className="sr-only">Description</h3>
                        <div className="text-base text-gray-700 space-y-6" dangerouslySetInnerHTML={{ __html: product.description }} />
                    </div>
                    <div className="mt-10">
                        <button
                            onClick={addToCart}
                            className="w-full bg-primary border border-transparent rounded-md py-3 px-8 flex items-center justify-center text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                        >
                            Add to Cart
                        </button>
                    </div>
                </div>
            </div>

            {/* Recommendations Section */}
            {/* Placeholder for now */}
            <div className="mt-16">
                <h2 className="text-2xl font-bold tracking-tight text-gray-900">Recommended for You</h2>
                <div className="mt-6 grid grid-cols-1 gap-y-10 gap-x-6 sm:grid-cols-2 lg:grid-cols-4 xl:gap-x-8">
                    {/* Render recommended products list here */}
                    <p className="text-gray-500">Recommendations coming soon...</p>
                </div>
            </div>
        </div>
    );
};

export default ProductDetailPage;
