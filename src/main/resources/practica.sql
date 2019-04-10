#<apartat>
#[1]-[]####################################################################################################
# Aquesta es la plantilla d autocorrecció per les queries.
# La consulta ha d anar entre els simbols @ i $
#@
#$
#</apartat>
#Un cop creades les taules...

#<apartat>
#[2]-[]####################################################################################################
# La query corresponent a l apartat 2 de l enunciat...
#@
#$
#</apartat>

#<apartat>
#[3]-[]####################################################################################################
#Etc...
#@
#$
#</apartat>

#<apartat>
#[4]-[]####################################################################################################
#Etc...
#@
#$
#</apartat>
#<apartat>
#[5]-[]####################################################################################################
#Etc...
#@
#$
#</apartat>
#<apartat>
#[6]-[]####################################################################################################
#Etc...
#@
#$
#</apartat>
#<apartat>
#[7]-[]####################################################################################################
#Etc...
#@
#$
#</apartat>
#<apartat>
#[8]-[]####################################################################################################
#Etc...
#@
#$
#</apartat>
#<apartat>
#[9]-[]####################################################################################################
#Etc...
#@
select * from  DOCTOR
where DOCTOR.HOSPITAL_COD = 22
order by DOCTOR.COGNOM desc;
#$
#</apartat>
#<apartat>
#[10]-[]####################################################################################################
#Etc...
#@
select count(1) FROM MALALT WHERE datediff(now(),DATA_NAIX) > 10950;
#$
#</apartat>
#<apartat>
#[11]-[]####################################################################################################
#Etc...
#@
select * from DOCTOR INNER JOIN HOSPITAL using (HOSPITAL_COD)
where HOSPITAL.NOM = 'La Paz';
#$
#</apartat>
#<apartat>
#[12]-[]####################################################################################################
#Etc...
#@
select avg(PLANTILLA.SALARI) FROM PLANTILLA
INNER JOIN HOSPITAL using (HOSPITAL_COD)
where HOSPITAL.NOM = 'La Paz';
#$
#</apartat>
#<apartat>
#[13]-[]####################################################################################################
#Etc...
#@
select HOSPITAL_COD, sum(SALARI)
FROM PLANTILLA
GROUP BY PLANTILLA.HOSPITAL_COD;

#$
#</apartat>
#<apartat>
#[14]-[]####################################################################################################
#Etc...
#@
select SALA.HOSPITAL_COD,count(SALA.SALA_COD)
FROM SALA LEFT JOIN INGRESSOS USING (SALA_COD)
where INGRESSOS.SALA_COD is NULL
group by SALA.hospital_cod
having count(SALA.SALA_COD) > 2;
#$
#</apartat>
#<apartat>
#[15]-[]####################################################################################################
#Etc...
#@
select MALALT.COGNOM, HOSPITAL.NOM from (MALALT LEFT JOIN INGRESSOS using(INSCRIPCIO))
  LEFT JOIN HOSPITAL USING (HOSPITAL_COD)
where DATA_NAIX BETWEEN '1940-01-01' AND '1960-12-31';
#$
#</apartat>