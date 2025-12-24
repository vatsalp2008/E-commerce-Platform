import { Request, Response } from 'express';
import prisma from '../db/prisma';

export const getProducts = async (req: Request, res: Response) => {
    try {
        const { category, search, page = 1, limit = 10 } = req.query;
        const skip = (Number(page) - 1) * Number(limit);

        const whereClause: any = {};

        if (category) {
            whereClause.category = String(category);
        }

        if (search) {
            whereClause.OR = [
                { name: { contains: String(search), mode: 'insensitive' } },
                { description: { contains: String(search), mode: 'insensitive' } },
            ];
        }

        const products = await prisma.product.findMany({
            where: whereClause,
            skip,
            take: Number(limit),
            orderBy: { createdAt: 'desc' },
        });

        const total = await prisma.product.count({ where: whereClause });

        res.json({
            products,
            pagination: {
                total,
                page: Number(page),
                pages: Math.ceil(total / Number(limit)),
            },
        });
    } catch (error) {
        console.error('Get products error:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

export const getProductById = async (req: Request, res: Response) => {
    try {
        const { id } = req.params;
        const product = await prisma.product.findUnique({
            where: { id: Number(id) },
        });

        if (!product) {
            return res.status(404).json({ message: 'Product not found' });
        }

        res.json(product);
    } catch (error) {
        console.error('Get product error:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

export const createProduct = async (req: Request, res: Response) => {
    try {
        const { name, description, price, imageUrl, category, stock } = req.body;
        const product = await prisma.product.create({
            data: {
                name,
                description,
                price: Number(price),
                imageUrl,
                category,
                stock: Number(stock)
            }
        });
        res.status(201).json(product);
    } catch (error) {
        console.error('Create product error:', error);
        res.status(500).json({ message: 'Server error' });
    }
}
