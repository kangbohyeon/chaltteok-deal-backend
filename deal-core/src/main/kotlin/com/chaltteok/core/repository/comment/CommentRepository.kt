package com.chaltteok.core.repository.comment

import com.chaltteok.core.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentCountProjection {
    val productId: Long
    val cnt: Long
}

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByCommentUuid(uuid: String): Comment?

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
        ORDER BY c.createdAt DESC
    """)
    fun findAllOrderByCreatedAtDesc(): List<Comment>

    @Query("""
        SELECT c.product.id AS productId, COUNT(c) AS cnt
        FROM Comment c
        WHERE c.product.id IN :productIds
        AND c.parentId IS NULL
        AND c.isSecret = false
        GROUP BY c.product.id
    """)
    fun countRootCommentsByProductIds(@Param("productIds") productIds: List<Long>): List<CommentCountProjection>
}
