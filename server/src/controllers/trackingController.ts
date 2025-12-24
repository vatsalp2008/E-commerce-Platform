import { Request, Response } from 'express';
import mongoose from 'mongoose';
import { UserBehavior } from '../models/UserBehavior';

export const trackAction = async (req: Request, res: Response) => {
    try {
        const userId = req.user?.userId;
        const { productId, action, meta } = req.body;

        if (!userId) {
            // In a real app we might track anonymous users with a session ID, but for now require auth
            return res.status(401).json({ message: 'Unauthorized' });
        }

        if (mongoose.connection.readyState !== 1) {
            return res.status(200).json({ message: 'Tracking skipped (DB offline)' });
        }

        await UserBehavior.create({
            userId,
            productId,
            action,
            meta,
        });

        res.status(201).json({ message: 'Action tracked' });
    } catch (error) {
        console.error('Track action error:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

export const getUserHistory = async (req: Request, res: Response) => {
    try {
        const userId = req.user?.userId;
        const history = await UserBehavior.find({ userId }).sort({ timestamp: -1 }).limit(50);
        res.json(history);
    } catch (error) {
        console.error('Get history error:', error);
        res.status(500).json({ message: 'Server error' });
    }
}
