import { Router } from 'express';
import { getCart, addToCart, removeFromCart } from '../controllers/cartController';
import { authenticate } from '../middlewares/authMiddleware';

const router = Router();

router.use(authenticate);

router.get('/', getCart);
router.post('/add', addToCart);
router.delete('/remove/:itemId', removeFromCart);

export default router;
