package com.chaltteok.core.repository.comment

import com.chaltteok.core.domain.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface CommentCountProjection {
    val productId: Long
    val cnt: Long
}

interface AverageRatingProjection {
    val productId: Long
    val avg: Double?
}

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByCommentUuid(uuid: String): Comment?

    @Query("SELECT c FROM Comment c JOIN FETCH c.product WHERE c.commentUuid = :uuid")
    fun findByCommentUuidWithProduct(@Param("uuid") uuid: String): Comment?

    @Query("""
        SELECT c FROM Comment c
        WHERE c.product.productUuid = :productUuid
        AND c.parentId IS NULL
        ORDER BY c.createdAt ASC
    """)
    fun findRootCommentsByProductUuid(@Param("productUuid") productUuid: String): List<Comment>

    @Query("""
        SELECT c FROM Comment c
        WHERE c.parentId IN :parentIds
        ORDER BY c.createdAt ASC
    """)
    fun findRepliesByParentIds(@Param("parentIds") parentIds: List<Long>): List<Comment>

    @Query("""
        SELECT c FROM Comment c
        WHERE c.product.productUuid = :productUuid
        AND c.parentId IS NULL
        ORDER BY c.createdAt ASC
    """)
    fun findRootCommentsByProductUuidPaged(@Param("productUuid") productUuid: String, pageable: Pageable): Page<Comment>

    @Query("""
        SELECT c FROM Comment c
        ORDER BY c.createdAt DESC
    """)
    fun findAllOrderByCreatedAtDesc(): List<Comment>

    @Query(value = """
        SELECT c FROM Comment c
        WHERE c.parentId IS NULL
        ORDER BY c.createdAt DESC
    """, countQuery = "SELECT COUNT(c) FROM Comment c WHERE c.parentId IS NULL")
    fun findRootCommentsPagedForOwner(pageable: Pageable): Page<Comment>

    @Query("""
        SELECT c.product.id AS productId, COUNT(c) AS cnt
        FROM Comment c
        WHERE c.product.id IN :productIds
        AND c.parentId IS NULL
        GROUP BY c.product.id
    """)
    fun countRootCommentsByProductIds(@Param("productIds") productIds: List<Long>): List<CommentCountProjection>

    @Query("""
        SELECT c.product.id AS productId, AVG(CAST(c.rating AS double)) AS avg
        FROM Comment c
        WHERE c.product.id IN :productIds
        AND c.parentId IS NULL
        AND c.rating IS NOT NULL
        GROUP BY c.product.id
    """)
    fun avgRatingByProductIds(@Param("productIds") productIds: List<Long>): List<AverageRatingProjection>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.product.id = :productId")
    fun deleteAllByProductId(@Param("productId") productId: Long)
}
