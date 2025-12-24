import { Router } from 'express';
import { trackAction, getUserHistory } from '../controllers/trackingController';
import { authenticate } from '../middlewares/authMiddleware';

const router = Router();

router.use(authenticate);

router.post('/', trackAction);
router.get('/history', getUserHistory);

export default router;
