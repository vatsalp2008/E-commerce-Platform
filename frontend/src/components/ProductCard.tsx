import { Star, ShoppingCart, Heart } from 'lucide-react';

interface ProductCardProps {
    id: string;
    name: string;
    price: number;
    image: string;
    category: string;
}

const ProductCard = ({ name, price, image, category }: ProductCardProps) => {
    return (
        <div className="group bg-white rounded-2xl shadow-sm hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100">
            <div className="relative aspect-square overflow-hidden bg-gray-100">
                <img
                    src={image || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1000&auto=format&fit=crop'}
                    alt={name}
                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                />
                <button className="absolute top-4 right-4 p-2 bg-white/80 backdrop-blur-sm rounded-full text-gray-600 hover:text-red-500 hover:bg-white transition-colors duration-200">
                    <Heart className="w-5 h-5" />
                </button>
                <div className="absolute inset-x-0 bottom-0 p-4 translate-y-full group-hover:translate-y-0 transition-transform duration-300">
                    <button className="w-full bg-primary-600 text-white py-2 rounded-lg font-semibold flex items-center justify-center shadow-lg hover:bg-primary-700 transition-colors">
                        <ShoppingCart className="w-4 h-4 mr-2" /> Add to Cart
                    </button>
                </div>
            </div>
            <div className="p-4">
                <span className="text-xs font-medium text-primary-600 uppercase tracking-wider">{category}</span>
                <h3 className="text-lg font-bold text-gray-900 mt-1 line-clamp-1">{name}</h3>
                <div className="flex items-center mt-2">
                    <div className="flex text-yellow-400">
                        {[...Array(5)].map((_, i) => (
                            <Star key={i} className="w-4 h-4 fill-current" />
                        ))}
                    </div>
                    <span className="text-xs text-gray-500 ml-2">(128 reviews)</span>
                </div>
                <div className="flex justify-between items-center mt-4">
                    <span className="text-xl font-extrabold text-gray-900">${price.toFixed(2)}</span>
                </div>
            </div>
        </div>
    );
};

export default ProductCard;
