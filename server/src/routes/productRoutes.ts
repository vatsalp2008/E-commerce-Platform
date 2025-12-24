import { Router } from 'express';
import { getProducts, getProductById, createProduct } from '../controllers/productController';
// import { authenticate } from '../middlewares/authMiddleware';

const router = Router();

router.get('/', getProducts);
router.get('/:id', getProductById);
router.post('/', createProduct); // In a real app, protect this with admin middleware

export default router;
