--UPDATE
update Fall22_S003_16_C_gives set crating = 5 where rgid = 'R15';
update Fall22_S003_16_Reviews set rRating = 5 where rID = 'R15';


--DELETE
delete from fall22_s003_16_c_gives where cgID = 'C21';
delete from fall22_s003_16_o_contains where ocID = 'Od21';
delete from Fall22_S003_16_Customer_phno where cID = 'C21';
delete from fall22_s003_16_customer where ctID = 'C21';
delete from fall22_s003_16_order where odID = 'Od21';
delete from fall22_s003_16_transaction where trID = 'T21';
update Fall22_S003_16_Reviews set rRating = 0 where rID = 'R21';


--INSERT
INSERT INTO Fall22_S003_16_Transaction(trID,trCI,trCT,trAmount) VALUES('T21','5932 5869 2304 5965','Discover','700');
INSERT INTO Fall22_S003_16_Order(odID,odDateTime,otID) VALUES('Od21',TO_TIMESTAMP('2022-10-01 06:10:22','YYYY-MM-DD HH24:MI:SS'),'T21');
INSERT INTO Fall22_S003_16_Customer(ctID,ctName,ctEmail,ctCity,ctState,coID) VALUES('C21','Reina Davila','reinadavila@gmail.com','Muncie','Indiana','Od21');
INSERT INTO Fall22_S003_16_Customer_phno(cID,phno) VALUES('C21','9846415014');
INSERT INTO Fall22_S003_16_Customer_phno(cID,phno) VALUES('C21','9961494202');
INSERT INTO Fall22_S003_16_O_contains(ocID,pcID,pqty) VALUES('Od21','P17',1);
INSERT INTO Fall22_S003_16_C_gives(rgID,cgID,cRating) VALUES('R17','C21',4);
