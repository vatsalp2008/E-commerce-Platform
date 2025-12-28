-- Seed Categories
INSERT INTO categories (id, name, description) VALUES 
(gen_random_uuid(), 'Electronics', 'Gadgets, devices, and accessories'),
(gen_random_uuid(), 'Clothing', 'Apparel, footwear, and fashion'),
(gen_random_uuid(), 'Books', 'Fiction, non-fiction, and educational material'),
(gen_random_uuid(), 'Home & Garden', 'Furniture, decor, and gardening tools'),
(gen_random_uuid(), 'Sports', 'Sports equipment, apparel, and accessories');

-- Store IDs for reference (using a techinque to get them back)
DO $$
DECLARE
    electronics_id UUID;
    clothing_id UUID;
    books_id UUID;
    home_id UUID;
    sports_id UUID;
BEGIN
    SELECT id INTO electronics_id FROM categories WHERE name = 'Electronics';
    SELECT id INTO clothing_id FROM categories WHERE name = 'Clothing';
    SELECT id INTO books_id FROM categories WHERE name = 'Books';
    SELECT id INTO home_id FROM categories WHERE name = 'Home & Garden';
    SELECT id INTO sports_id FROM categories WHERE name = 'Sports';

    -- Seed Products
    -- Electronics
    INSERT INTO products (id, name, description, price, stock_quantity, image_url, category_id, brand) VALUES 
    (gen_random_uuid(), 'Smartphone X', 'Latest flagship smartphone', 999.99, 50, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9', electronics_id, 'TechBrand'),
    (gen_random_uuid(), 'Laptop Pro', 'High-performance laptop for pros', 1499.99, 30, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853', electronics_id, 'ComputeCo'),
    (gen_random_uuid(), 'Wireless Earbuds', 'Noise-canceling earbuds', 199.99, 100, 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df', electronics_id, 'AudioTech'),
    (gen_random_uuid(), 'Smart Watch', 'Fitness tracking and notifications', 249.99, 75, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30', electronics_id, 'WearableX');

    -- Clothing
    INSERT INTO products (id, name, description, price, stock_quantity, image_url, category_id, brand) VALUES 
    (gen_random_uuid(), 'Classic T-Shirt', '100% Cotton basic tee', 19.99, 200, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab', clothing_id, 'ApparelCo'),
    (gen_random_uuid(), 'Denim Jeans', 'Slim fit blue jeans', 59.99, 150, 'https://images.unsplash.com/photo-1542272604-787c3835535d', clothing_id, 'DenimLux'),
    (gen_random_uuid(), 'Leather Jacket', 'Premium black leather jacket', 199.99, 25, 'https://images.unsplash.com/photo-1521223890158-f9f7c3d5d504', clothing_id, 'FashionHub'),
    (gen_random_uuid(), 'Summer Dress', 'Flowy floral summer dress', 49.99, 60, 'https://images.unsplash.com/photo-1515372039744-b8f02a3ae446', clothing_id, 'StyleMe');

    -- Books
    INSERT INTO products (id, name, description, price, stock_quantity, image_url, category_id, brand) VALUES 
    (gen_random_uuid(), 'The Great Gatsby', 'Classic literature novel', 14.99, 100, 'https://images.unsplash.com/photo-1544947950-fa07a98d237f', books_id, 'ClassicPub'),
    (gen_random_uuid(), 'Atomic Habits', 'Self-help book on habits', 18.99, 250, 'https://images.unsplash.com/photo-1531243269054-5ebf6f3ad0e6', books_id, 'SelfHelpPress'),
    (gen_random_uuid(), 'Learn Java', 'Programming guide for Java', 45.99, 80, 'https://images.unsplash.com/photo-1587620962725-abab7fe55159', books_id, 'DevBooks'),
    (gen_random_uuid(), 'Cooking Masterclass', 'Gourmet recipes and tips', 34.99, 45, 'https://images.unsplash.com/photo-1556910103-1c02745aae4d', books_id, 'FoodieBooks');

    -- Home & Garden
    INSERT INTO products (id, name, description, price, stock_quantity, image_url, category_id, brand) VALUES 
    (gen_random_uuid(), 'Coffee Maker', 'Drip coffee machine with timer', 89.99, 40, 'https://images.unsplash.com/photo-1517668808822-9eaa02f2a9e0', home_id, 'HomeKitchen'),
    (gen_random_uuid(), 'Desk Lamp', 'LED adjustable desk lamp', 29.99, 120, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c', home_id, 'LightCo'),
    (gen_random_uuid(), 'Garden Shovel', 'Ergonomic garden tool', 15.99, 90, 'https://images.unsplash.com/photo-1617576621334-19253027b409', home_id, 'GreenThumb'),
    (gen_random_uuid(), 'Indoor Plant', 'Easy-care snake plant', 24.99, 70, 'https://images.unsplash.com/photo-1512428559087-560fa5ceab42', home_id, 'NatureIn');

    -- Sports
    INSERT INTO products (id, name, description, price, stock_quantity, image_url, category_id, brand) VALUES 
    (gen_random_uuid(), 'Basketball', 'Official size and weight', 29.99, 85, 'https://images.unsplash.com/photo-1546519638-68e109498ffc', sports_id, 'SportPro'),
    (gen_random_uuid(), 'Yoga Mat', 'Non-slip eco-friendly mat', 39.99, 110, 'https://images.unsplash.com/photo-1592432676556-26d575816997', sports_id, 'ZenFlex'),
    (gen_random_uuid(), 'Running Shoes', 'Lightweight breathable shoes', 129.99, 40, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', sports_id, 'SpeedRun'),
    (gen_random_uuid(), 'Dumbbells Set', 'Adjustable weight set', 89.99, 20, 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438', sports_id, 'IronFit');
END $$;
