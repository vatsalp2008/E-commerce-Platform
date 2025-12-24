export interface Product {
    id: number;
    name: string;
    description: string;
    price: number;
    imageUrl: string;
    category: string;
    stock: number;
}

export interface CartItem {
    id: number;
    productId: number;
    quantity: number;
    product: Product;
}

export interface Cart {
    id: number;
    items: CartItem[];
}
