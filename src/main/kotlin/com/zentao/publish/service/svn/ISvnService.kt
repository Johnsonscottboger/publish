package com.zentao.publish.service.svn

import com.zentao.publish.viewmodel.SvnCommitInput
import com.zentao.publish.viewmodel.SvnList

interface ISvnService {

    fun list(projectId: String) : List<SvnList>

    fun version(projectId: String) : String

    fun create(projectId: String) : String

    fun commit(input: SvnCommitInput) : String
}