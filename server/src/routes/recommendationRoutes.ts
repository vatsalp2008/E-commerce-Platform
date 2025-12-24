import { Router } from 'express';
import { getPersonalizedRecommendations } from '../controllers/recommendationController';
import { authenticate } from '../middlewares/authMiddleware';

const router = Router();

router.use(authenticate);

router.get('/personalized/:userId', getPersonalizedRecommendations);

export default router;
