# LDAP后端项目

## 容器创建

docker run --name nginx -ti -p 80:80 -v /etc/localtime:/etc/localtime -v /opt/ldap/nginx:/usr/share/nginx/html nginx:latest

docker run --name openjdk -ti -p 8080:8080 -v /etc/localtime:/etc/localtime -v /opt/ldap/service:/opt openjdk:latest
添加字体支持：
https://www.cnblogs.com/yucongblog/p/13994915.html

docker run --name nginx -ti -p 80:80 -v /etc/localtime:/etc/localtime -v /opt/ldap/nginx:/usr/share/nginx/html nginx:latest
