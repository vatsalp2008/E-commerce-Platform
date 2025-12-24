import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Cart } from '../types';

const CartPage: React.FC = () => {
    const [cart, setCart] = useState<Cart | null>(null);

    const fetchCart = async () => {
        try {
            const { data } = await api.get('/cart');
            setCart(data);
        } catch (error) {
            console.error('Failed to fetch cart', error);
        }
    };

    useEffect(() => {
        fetchCart();
    }, []);

    const handleRemove = async (itemId: number) => {
        try {
            await api.delete(`/cart/remove/${itemId}`);
            fetchCart();
        } catch (error) {
            console.error("Failed to remove item", error);
        }
    }

    const handleCheckout = async () => {
        try {
            await api.post('/orders');
            alert('Order placed successfully!');
            fetchCart();
        } catch (error) {
            alert('Checkout failed');
        }
    }

    if (!cart) return <div className="p-8">Your cart is empty or loading...</div>;
    if (cart.items.length === 0) return <div className="p-8">Your cart is empty.</div>;

    const total = cart.items.reduce((sum, item) => sum + item.product.price * item.quantity, 0);

    return (
        <div className="max-w-2xl mx-auto py-16 px-4 sm:py-24 sm:px-6 lg:max-w-7xl lg:px-8">
            <h1 className="text-3xl font-extrabold tracking-tight text-gray-900 sm:text-4xl">Shopping Cart</h1>
            <div className="mt-12 lg:grid lg:grid-cols-12 lg:gap-x-12 lg:items-start xl:gap-x-16">
                <section aria-labelledby="cart-heading" className="lg:col-span-7">
                    <ul role="list" className="border-t border-b border-gray-200 divide-y divide-gray-200">
                        {cart.items.map((item) => (
                            <li key={item.id} className="flex py-6 sm:py-10">
                                <div className="flex-shrink-0">
                                    <img
                                        src={item.product.imageUrl || 'https://via.placeholder.com/150'}
                                        alt={item.product.name}
                                        className="w-24 h-24 rounded-md object-center object-cover sm:w-48 sm:h-48"
                                    />
                                </div>

                                <div className="ml-4 flex-1 flex flex-col justify-between sm:ml-6">
                                    <div className="relative pr-9 sm:grid sm:grid-cols-2 sm:gap-x-6 sm:pr-0">
                                        <div>
                                            <div className="flex justify-between">
                                                <h3 className="text-sm">
                                                    <a href={`/products/${item.productId}`} className="font-medium text-gray-700 hover:text-gray-800">
                                                        {item.product.name}
                                                    </a>
                                                </h3>
                                            </div>
                                            <p className="mt-1 text-sm font-medium text-gray-900">${item.product.price}</p>
                                        </div>

                                        <div className="mt-4 sm:mt-0 sm:pr-9">
                                            <label htmlFor={`quantity-${item.id}`} className="sr-only">
                                                Quantity, {item.product.name}
                                            </label>
                                            <p className="text-gray-500">Qty {item.quantity}</p>

                                            <div className="absolute top-0 right-0">
                                                <button
                                                    onClick={() => handleRemove(item.id)}
                                                    type="button"
                                                    className="-m-2 p-2 inline-flex text-gray-400 hover:text-gray-500"
                                                >
                                                    <span className="sr-only">Remove</span>
                                                    <span className="text-red-500 font-bold">X</span>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </li>
                        ))}
                    </ul>
                </section>

                {/* Order summary */}
                <section
                    aria-labelledby="summary-heading"
                    className="mt-16 bg-gray-50 rounded-lg px-4 py-6 sm:p-6 lg:p-8 lg:mt-0 lg:col-span-5"
                >
                    <h2 id="summary-heading" className="text-lg font-medium text-gray-900">
                        Order summary
                    </h2>

                    <dl className="mt-6 space-y-4">
                        <div className="flex items-center justify-between border-t border-gray-200 pt-4">
                            <dt className="text-base font-medium text-gray-900">Order total</dt>
                            <dd className="text-base font-medium text-gray-900">${total.toFixed(2)}</dd>
                        </div>
                    </dl>

                    <div className="mt-6">
                        <button
                            onClick={handleCheckout}
                            type="submit"
                            className="w-full bg-primary border border-transparent rounded-md shadow-sm py-3 px-4 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                        >
                            Checkout
                        </button>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default CartPage;
