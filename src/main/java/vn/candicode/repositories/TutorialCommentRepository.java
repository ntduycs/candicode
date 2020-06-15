package vn.candicode.repositories;

import vn.candicode.models.TutorialCommentEntity;
import vn.candicode.models.TutorialEntity;

import java.util.List;

public interface TutorialCommentRepository {
    List<TutorialCommentEntity> findAllByTutorialWithLimit(TutorialEntity tutorial, Integer limit);

    Long countAllByTutorial(TutorialEntity tutorial);
}
