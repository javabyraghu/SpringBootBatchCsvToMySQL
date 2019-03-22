drop table  if exists prodstab;
CREATE TABLE prodstab (               
            pid int(11) DEFAULT NULL,           
            pname varchar(25) DEFAULT NULL,     
            pcost double DEFAULT NULL           
          )