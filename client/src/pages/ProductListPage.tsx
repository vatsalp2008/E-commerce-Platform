import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import { Product } from '../types';

const ProductListPage: React.FC = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [search, setSearch] = useState('');
    const [category, setCategory] = useState('');

    const fetchProducts = async () => {
        try {
            const { data } = await api.get('/products', {
                params: { search, category }
            });
            setProducts(data.products);
        } catch (error) {
            console.error('Failed to fetch products', error);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, [category]); // Re-fetch on category change

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        fetchProducts();
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-col sm:flex-row justify-between items-center gap-4">
                <h1 className="text-2xl font-bold text-gray-900">Products</h1>
                <form onSubmit={handleSearch} className="flex gap-2 w-full sm:w-auto">
                    <input
                        type="text"
                        placeholder="Search..."
                        className="border rounded px-3 py-2 w-full"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                    />
                    <button type="submit" className="bg-primary text-white px-4 py-2 rounded">Search</button>
                </form>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {products.map((product) => (
                    <Link key={product.id} to={`/products/${product.id}`} className="group relative block bg-white rounded-lg shadow overflow-hidden hover:shadow-lg transition-shadow">
                        <div className="aspect-w-1 aspect-h-1 w-full overflow-hidden bg-gray-200 xl:aspect-w-7 xl:aspect-h-8">
                            <img
                                src={product.imageUrl || 'https://via.placeholder.com/300'}
                                alt={product.name}
                                className="h-48 w-full object-cover object-center group-hover:opacity-75"
                            />
                        </div>
                        <div className="p-4">
                            <h3 className="text-sm text-gray-700">{product.name}</h3>
                            <p className="mt-1 text-lg font-medium text-gray-900">${product.price}</p>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default ProductListPage;
