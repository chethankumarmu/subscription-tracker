package com.brightloop.subscription.support.repository;

import com.brightloop.subscription.support.entity.FaqArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqArticleRepository extends JpaRepository<FaqArticle, Long> {

    List<FaqArticle> findByPublishedTrue();

    @Query(value = "SELECT * FROM faq_articles " +
                   "WHERE is_published = true AND (" +
                   "LOWER(question) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
                   "LOWER(answer) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
                   "LOWER(category) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
                   ":term = ANY(keywords))", nativeQuery = true)
    List<FaqArticle> searchPublished(@Param("term") String term);
}

