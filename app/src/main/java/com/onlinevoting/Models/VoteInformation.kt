package com.sunayanpradhan.onlinevoting.Models


data class VoteInformation(
    var voteId:String,
    var voteTitle:String,
    var voteLogo:String,
    var voteStartTime:Long,
    var voteEndTime:Long,
    var resultDate:Long
)
{
    constructor():this(
        "",
        "",
        "",
        0,
        0,
        0
        )
}
