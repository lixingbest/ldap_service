#!/usr/bin/expect

set timeout 1

set username [lindex $argv 0]

spawn su $username
expect {
  "*密码*" {
    send "Jnhg2013\r"
    exp_continue
  }
}

expect {
  "*Current Password*" {
    send "Jnhg2013\r"
    exp_continue
  }
}

expect {
  "*新的密码*" {
    send "Jnhg2023\r"
    exp_continue
  }
}

expect {
  "*重新输入新的密码*" {
     send "Jnhg2023\r"
     exp_continue
  }
}

