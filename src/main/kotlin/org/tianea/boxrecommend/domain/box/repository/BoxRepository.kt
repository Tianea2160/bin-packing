package org.tianea.boxrecommend.domain.box.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.tianea.boxrecommend.domain.box.entity.Box

@Repository
interface BoxRepository : JpaRepository<Box, Long> {
}