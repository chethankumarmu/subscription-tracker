package com.brightloop.subscription.support.repository;

import com.brightloop.subscription.support.entity.FaqArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqArticleRepository extends JpaRepository<FaqArticle, Long> {
    List<FaqArticle> findByIsPublishedTrue();
    List<FaqArticle> findByCategoryAndIsPublishedTrue(String category);
}
