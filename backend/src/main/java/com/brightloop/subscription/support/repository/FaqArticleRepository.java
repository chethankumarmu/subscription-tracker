package com.brightloop.subscription.support.repository;

import com.brightloop.subscription.support.entity.FaqArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqArticleRepository extends JpaRepository<FaqArticle, Long> {
    List<FaqArticle> findByIsPublishedTrue();
    List<FaqArticle> findByCategoryAndIsPublishedTrue(String category);

    @Query("SELECT f FROM FaqArticle f WHERE f.isPublished = true AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(f.answer) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<FaqArticle> searchPublished(@Param("query") String query);
}
