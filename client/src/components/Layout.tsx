import React from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Layout: React.FC = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex">
                            <Link to="/" className="flex-shrink-0 flex items-center text-xl font-bold text-primary">
                                AI Shop
                            </Link>
                            <div className="ml-6 flex space-x-8 items-center">
                                <Link to="/" className="text-gray-900 hover:text-primary">Products</Link>
                                {user && <Link to="/orders" className="text-gray-900 hover:text-primary">Orders</Link>}
                            </div>
                        </div>
                        <div className="flex items-center">
                            <Link to="/cart" className="p-2 text-gray-400 hover:text-gray-500">
                                Cart
                            </Link>
                            <div className="ml-4 flex items-center">
                                {user ? (
                                    <div className="flex items-center space-x-4">
                                        <span className="text-sm text-gray-700">Hi, {user.name || user.email}</span>
                                        <button
                                            onClick={handleLogout}
                                            className="text-sm font-medium text-red-600 hover:text-red-500"
                                        >
                                            Logout
                                        </button>
                                    </div>
                                ) : (
                                    <div className="space-x-4">
                                        <Link to="/login" className="text-sm font-medium text-gray-700 hover:text-gray-900">Login</Link>
                                        <Link to="/register" className="text-sm font-medium text-primary hover:text-blue-500">Register</Link>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </nav>
            <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;
