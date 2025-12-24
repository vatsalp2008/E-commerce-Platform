import mongoose from 'mongoose';

export const connectMongoDB = async () => {
    try {
        const mongoURI = process.env.MONGO_URI || 'mongodb://localhost:27017/ecommerce_tracking';
        await mongoose.connect(mongoURI);
        console.log('MongoDB connected successfully');
    } catch (error) {
        console.warn('MongoDB connection failed (running in fallback mode):', error);
        // process.exit(1);
    }
};
