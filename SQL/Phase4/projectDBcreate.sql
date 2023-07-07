CREATE TABLE Fall22_S003_16_Seller(slrID varchar(255) NOT NULL PRIMARY KEY, slrName varchar(255),slrCity varchar(255),slrState varchar(255));
    

CREATE TABLE Fall22_S003_16_Transaction(trID varchar(255) NOT NULL PRIMARY KEY, trCI varchar(255), trCT varchar(255), trAmount float);
    

CREATE TABLE Fall22_S003_16_Product(pdID varchar(255) NOT NULL PRIMARY KEY, pdName varchar(255), pdCost float, pdMC varchar(255), 
psID varchar(255), FOREIGN KEY (psID) REFERENCES Fall22_S003_16_Seller(slrID) );


CREATE TABLE Fall22_S003_16_Order(odID varchar(255) NOT NULL PRIMARY KEY, odDateTime TIMESTAMP,
otID varchar(255), FOREIGN KEY(otID) REFERENCES Fall22_S003_16_Transaction(trID));


CREATE TABLE Fall22_S003_16_Customer(crID varchar(255) NOT NULL PRIMARY KEY, ctName varchar(255), ctEmail varchar(255), 
ctCity varchar(255),ctState varchar(255), coID varchar(255), FOREIGN KEY (coID) REFERENCES Fall22_S003_16_Order(odID));


CREATE TABLE Fall22_S003_16_Customer_phno(cID varchar(255) NOT NULL, phno float NOT NULL,
CONSTRAINT  cph PRIMARY KEY (cID,phno), 
CONSTRAINT  cid FOREIGN KEY (cID) REFERENCES Fall22_S003_16_Customer(crID));


CREATE TABLE Fall22_S003_16_Reviews(rID varchar(255) NOT NULL PRIMARY KEY, rRating float, 
rpID varchar(255), FOREIGN KEY (rpID) REFERENCES Fall22_S003_16_product(pdID));


CREATE TABLE Fall22_S003_16_O_contains(ocID varchar(255) NOT NULL, pcID varchar(255) NOT NULL, pqty int,
CONSTRAINT  oc PRIMARY KEY(ocID,pcID), 
CONSTRAINT  ocid FOREIGN KEY (ocID) REFERENCES Fall22_S003_16_Order(odID),
CONSTRAINT  pcid FOREIGN KEY (pcID) REFERENCES Fall22_S003_16_Product(pdID));


CREATE TABLE Fall22_S003_16_C_gives(rgID varchar(255) NOT NULL, cgID varchar(255) NOT NULL, cRating int,
CONSTRAINT  cg PRIMARY KEY (rgID,cgID), 
CONSTRAINT  rgid FOREIGN KEY (rgID) REFERENCES Fall22_S003_16_Reviews(rID),
CONSTRAINT  cgid FOREIGN KEY (cgID) REFERENCES Fall22_S003_16_Customer(crID));



