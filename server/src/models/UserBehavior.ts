import mongoose from 'mongoose';

const userBehaviorSchema = new mongoose.Schema({
    userId: { type: Number, required: true },
    productId: { type: Number, required: true },
    action: { type: String, enum: ['view', 'click', 'add_to_cart', 'purchase'], required: true },
    timestamp: { type: Date, default: Date.now },
    meta: { type: mongoose.Schema.Types.Mixed }, // Any additional data
});

export const UserBehavior = mongoose.model('UserBehavior', userBehaviorSchema);
