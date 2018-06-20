package com.enovlab.yoop.data.entity.enums

/**
 * Created by mtosk on 3/21/2018.
 */
enum class AssignmentStatus {
    //The owner has decided to revoke this assignment
    REVOKED,
    //The assignee has returned this assignment after accepting
    RETURNED,
    //The assignee declined this assignment
    DECLINED,
    //The assignee has not taken any action yet
    PENDING,
    //The assignee has accepted this assignment
    CONFIRMED;

    fun unassigned() = this == REVOKED || this == RETURNED || this == DECLINED
}