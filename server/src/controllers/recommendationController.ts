import { Request, Response } from 'express';
import axios from 'axios';
import prisma from '../db/prisma';

const ML_SERVICE_URL = 'http://localhost:5001';

export const getPersonalizedRecommendations = async (req: Request, res: Response) => {
    try {
        const userId = req.params.userId;
        // Call ML Service
        const mlResponse = await axios.get(`${ML_SERVICE_URL}/recommend/personalized/${userId}`);
        const productIds = mlResponse.data.recommendations; // Array of IDs

        if (!productIds || productIds.length === 0) {
            return res.json([]);
        }

        // Fetch details from DB
        const products = await prisma.product.findMany({
            where: { id: { in: productIds } }
        });

        // Maintain order
        const sortedProducts = productIds.map((id: number) => products.find((p) => p.id === id)).filter(Boolean);

        res.json(sortedProducts);
    } catch (error) {
        console.error('Recommendation error:', error);
        // Fallback to latest products if ML fails
        const fallback = await prisma.product.findMany({ take: 5, orderBy: { createdAt: 'desc' } });
        res.json(fallback);
    }
};
