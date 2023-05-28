c = LOAD '$G' USING PigStorage(',') AS (node:int, adj:int);
g1 = group c by node;
g2 = foreach g1 generate COUNT(c) as ct;
g3 = group g2 by ct;
g4 = foreach g3 generate group,COUNT(g2);
STORE g4 INTO '$O' USING PigStorage ('\t');
