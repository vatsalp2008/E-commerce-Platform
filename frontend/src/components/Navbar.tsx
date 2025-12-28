import { ShoppingCart, User, Package, Search, Menu, X } from 'lucide-react';
import { useState } from 'react';

const Navbar = () => {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <nav className="bg-white shadow-md sticky top-0 z-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-16">
                    <div className="flex items-center">
                        <span className="text-2xl font-bold text-primary-600 flex items-center">
                            <Package className="mr-2" /> E-Shop
                        </span>
                    </div>

                    {/* Desktop Menu */}
                    <div className="hidden md:flex items-center space-x-8">
                        <a href="/" className="text-gray-700 hover:text-primary-600 font-medium transition duration-150">Home</a>
                        <a href="/products" className="text-gray-700 hover:text-primary-600 font-medium transition duration-150">Products</a>
                        <div className="relative">
                            <input
                                type="text"
                                placeholder="Search products..."
                                className="pl-10 pr-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-primary-500 w-64"
                            />
                            <Search className="absolute left-3 top-2.5 text-gray-400 h-5 w-5" />
                        </div>
                        <a href="/cart" className="relative p-2 text-gray-700 hover:text-primary-600 transition duration-150">
                            <ShoppingCart />
                            <span className="absolute top-0 right-0 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">0</span>
                        </a>
                        <a href="/login" className="p-2 text-gray-700 hover:text-primary-600 transition duration-150">
                            <User />
                        </a>
                    </div>

                    {/* Mobile menu button */}
                    <div className="md:hidden flex items-center">
                        <button
                            onClick={() => setIsOpen(!isOpen)}
                            className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-primary-500"
                        >
                            {isOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
                        </button>
                    </div>
                </div>
            </div>

            {/* Mobile Menu */}
            {isOpen && (
                <div className="md:hidden bg-white border-t border-gray-200 py-4 px-2 space-y-2">
                    <a href="/" className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-primary-600">Home</a>
                    <a href="/products" className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-primary-600">Products</a>
                    <a href="/cart" className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-primary-600">Cart</a>
                    <a href="/login" className="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:bg-gray-100 hover:text-primary-600">Profile</a>
                </div>
            )}
        </nav>
    );
};

export default Navbar;
