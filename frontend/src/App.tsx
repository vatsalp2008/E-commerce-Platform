import Navbar from './components/Navbar';
import ProductCard from './components/ProductCard';

function App() {
  const featuredProducts = [
    { id: '1', name: 'Premium Wireless Headphones', price: 299.99, category: 'Electronics', image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=1000' },
    { id: '2', name: 'Smart Fitness Watch', price: 199.99, category: 'Wearables', image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=1000' },
    { id: '3', name: 'Anti-Blue Light Glasses', price: 49.99, category: 'Accessories', image: 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?q=80&w=1000' },
    { id: '4', name: 'Ergonomic Desk Chair', price: 450.00, category: 'Furniture', image: 'https://images.unsplash.com/photo-1592078615290-033ee584e267?q=80&w=1000' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      {/* Hero Section */}
      <header className="relative bg-white pt-16 pb-32 overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="lg:w-1/2">
            <h1 className="text-5xl lg:text-7xl font-extrabold text-gray-900 tracking-tight leading-tight">
              Elevate Your <span className="text-primary-600">Life</span> with Premium Quality.
            </h1>
            <p className="mt-6 text-xl text-gray-600 max-w-2xl">
              Discover our curated collection of tech, lifestyle, and home essentials designed for those who appreciate excellence.
            </p>
            <div className="mt-10 flex space-x-4">
              <button className="bg-primary-600 text-white px-8 py-4 rounded-xl font-bold hover:bg-primary-700 shadow-xl transition-all duration-300 transform hover:-translate-y-1">
                Shop Collection
              </button>
              <button className="bg-white text-gray-900 px-8 py-4 rounded-xl font-bold border-2 border-gray-100 hover:border-primary-600 transition-all duration-300 shadow-sm">
                View Deals
              </button>
            </div>
          </div>
        </div>
        <div className="absolute top-0 right-0 w-1/2 h-full bg-primary-50 rounded-l-[100px] -z-0 hidden lg:block overflow-hidden">
          <img
            src="https://images.unsplash.com/photo-1441986300917-64674bd600d8?q=80&w=1000"
            alt="Hero"
            className="w-full h-full object-cover opacity-80"
          />
        </div>
      </header>

      {/* Featured Products */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-16 relative z-20">
        <div className="flex justify-between items-end mb-8">
          <div>
            <h2 className="text-3xl font-bold text-gray-900">Featured Products</h2>
            <p className="text-gray-500 mt-2">Handpicked items for our community</p>
          </div>
          <a href="/products" className="text-primary-600 font-bold hover:underline mb-2">View All →</a>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {featuredProducts.map((product) => (
            <ProductCard key={product.id} {...product} />
          ))}
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 mt-24 py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-gray-500">
          <p>© 2025 E-Shop Platform. Built with Event-Driven Microservices Architecture.</p>
        </div>
      </footer>
    </div>
  );
}

export default App;
