-- Most rated product
select * from fall22_s003_16_product where pdid in 
(select rpid from fall22_s003_16_reviews where rrating = (select max(rrating) from fall22_s003_16_reviews));

--PDID     PDNAME     PDCOST   PDMC
--P17      iPhone     700      Apple

-- Most Sold product(quantity)
select * from fall22_s003_16_product p
where p.pdid in (select pcid from fall22_s003_16_o_contains o group by pcid
having sum(o.pqty) = (select max(sum(pqty)) from fall22_s003_16_o_contains group by pcid));

--PDID     PDNAME     PDCOST   PDMC
--P17      iPhone     700      Apple

--Products ordered in Combination
select f.combinations,count(f.combinations) as qty from(select ocid, LISTAGG(pcid, ',') WITHIN GROUP (ORDER BY pcid) as combinations
from fall22_s003_16_o_contains where ocid in (select  distinct ocid from fall22_s003_16_o_contains o1 
where ocid in(select ocid from fall22_s003_16_o_contains o2 where o1.ocid = o2.ocid and o1.pcid<>o2.pcid)) group by ocid)f
group by f.combinations;

-- COMBINATIONS QTY
-- P17,P5         5
-- ....          ...

--Best Seller
select f.qty,s.* from(select sum(o.pqty) as qty,s.slrid from fall22_s003_16_o_contains o, fall22_s003_16_product p,fall22_s003_16_seller s
where o.pcid = p.pdid and p.psid = s.slrid group by s.slrid
having sum(o.pqty) = (select max(sum(o.pqty)) from fall22_s003_16_o_contains o, fall22_s003_16_product p,fall22_s003_16_seller s
where o.pcid = p.pdid and p.psid = s.slrid group by s.slrid)) f, fall22_s003_16_seller s
where s.slrid = f.slrid;

-- QTY SLRID SLRNAME SLRCITY     SLRSTATE
-- 23  S3    Asurion Allapattah  Florida

--RollUp
select rgid, sum(crating)/count(cgid) as TotRating from fall22_s003_16_c_gives group by rollup(rgid);

-- RGID TOTRATING
-- R1   3
-- .... ....

--CUBE
select o.odid,extract(day from o.oddatetime) as O_Date ,sum(t.tramount)as Total 
from fall22_s003_16_order o, fall22_s003_16_transaction t
where o.otid = t.trid group by cube(o.odid, extract(day from o.oddatetime));

-- ODID   ODDATE  TOTAL
--                28277.9
--        1       3548.84
-- OD1            2.99 
-- ...    ....    .....

--Order by, Fetch
select c.ctid,c.ctname,t.tramount from fall22_s003_16_customer c,fall22_s003_16_order o ,fall22_s003_16_transaction t
where o.odid = c.coid and t.trid = o.otid and o.otid in
(select trid from fall22_s003_16_transaction order by tramount DESC
fetch first 5 rows only) order by t.tramount desc;

-- CTID  CTNAME       TRAMOUNT
-- C21   Reina Davila 1999.98
-- ...   ...          ....

--OVER
SELECT distinct t.trct,sum(t.tramount) OVER (PARTITION BY t.trct) AS TotValue, 
count(*) over (PARTITION BY t.trct) as OrderCount
FROM fall22_s003_16_transaction t, fall22_s003_16_order o where t.trid = o.otid  order by TotValue desc ;
-- TRCT       TOTVALUE      ORDERCOUNT
-- Discover   10555.72      13
-- ...        ...           ....




