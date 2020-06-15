package vn.candicode.repositories;

import org.springframework.stereotype.Repository;
import vn.candicode.models.TutorialCommentEntity;
import vn.candicode.models.TutorialEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class TutorialCommentRepositoryImpl implements TutorialCommentRepository {
    @PersistenceContext
    EntityManager entityManager;


    @Override
    public List<TutorialCommentEntity> findAllByTutorialWithLimit(TutorialEntity tutorial, Integer limit) {
        TypedQuery<TutorialCommentEntity> query = entityManager.createQuery(
            "select cmt from TutorialCommentEntity cmt where cmt.tutorial = : tutorial",
            TutorialCommentEntity.class
        );

        query.setParameter("tutorial", tutorial);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public Long countAllByTutorial(TutorialEntity tutorial) {
        TypedQuery<Long> query = entityManager.createQuery(
            "select count(distinct cmt.tutorialCommentId) from TutorialCommentEntity cmt where cmt.tutorial = :tutorial",
            Long.class
        );

        query.setParameter("tutorial", tutorial);

        return query.getSingleResult();
    }
}
