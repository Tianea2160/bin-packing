package org.tianea.boxrecommend.domain.recommend.result.document

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

data class ScoreCoordinate(
    @Field(type = FieldType.Integer)
    val level: Int,
    
    @Field(type = FieldType.Boolean)
    val isHard: Boolean,
    
    @Field(type = FieldType.Integer)
    val score: Int,
    
    @Field(type = FieldType.Keyword)
    val constraintName: String,
    
    @Field(type = FieldType.Text)
    val constraintDescription: String
)

data class AssignmentDetail(
    @Field(type = FieldType.Long)
    val itemId: Long,
    
    @Field(type = FieldType.Long)
    val binId: Long?,
    
    @Field(type = FieldType.Integer)
    val x: Int?,
    
    @Field(type = FieldType.Integer)
    val y: Int?,
    
    @Field(type = FieldType.Integer)
    val z: Int?,
    
    @Field(type = FieldType.Keyword)
    val rotation: String?
)
