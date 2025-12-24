import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

const products = [
    {
        name: 'Wireless Headphones',
        description: 'Noise-cancelling wireless headphones with 20h battery life.',
        price: 99.99,
        category: 'Electronics',
        imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80',
        stock: 50,
    },
    {
        name: 'Smart Watch',
        description: 'Fitness tracker with heart rate monitor.',
        price: 149.99,
        category: 'Electronics',
        imageUrl: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80',
        stock: 30,
    },
    {
        name: 'Gaming Mouse',
        description: 'High-precision RGB gaming mouse.',
        price: 49.99,
        category: 'Electronics',
        imageUrl: 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&q=80',
        stock: 100,
    },
    {
        name: 'Mechanical Keyboard',
        description: 'Tactile mechanical keyboard for typing and gaming.',
        price: 89.99,
        category: 'Electronics',
        imageUrl: 'https://images.unsplash.com/photo-1587829741301-dc798b91a91e?w=800&q=80',
        stock: 45,
    },
    {
        name: 'Laptop Backpack',
        description: 'Water-resistant backpack for 15-inch laptops.',
        price: 39.99,
        category: 'Accessories',
        imageUrl: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80',
        stock: 200,
    },
    {
        name: 'Smartphone Stand',
        description: 'Adjustable aluminum stand for phones and tablets.',
        price: 15.99,
        category: 'Accessories',
        imageUrl: 'https://images.unsplash.com/photo-1586771107445-d3ca888129ff?w=800&q=80',
        stock: 150,
    },
];

async function main() {
    console.log('Seeding products...');
    for (const product of products) {
        await prisma.product.create({
            data: product,
        });
    }
    console.log('Seeding finished.');
}

main()
    .catch((e) => {
        console.error(e);
        process.exit(1);
    })
    .finally(async () => {
        await prisma.$disconnect();
    });
