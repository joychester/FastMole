@echo off

cd C:\PPC\Mole\target
java -cp .\test;.\lib\*;.\release\mole-0.1.jar mole.ppc.PPCEngine > log.txt