//UDMv3.2.2


var exclude=true; var agt=navigator.userAgent.toLowerCase();
var ie=false; var ie4=false; var ie5=false; var op5=false; var konqi=false;
if (document.all&&(agt.indexOf('msie')!=-1)) {
ie=true; ie4=true; exclude=false;
if (agt.indexOf('msie 4')==-1) { ie5=true; ie4=false; }
if (agt.indexOf('opera')!=-1) { ie=false; ie4=false; ie5=false; op5=true; }
}
var ns6=false; var mz7=false;
if (document.getElementById&&!ie) {
ns6=true; exclude=false;
if (agt.indexOf('netscape6')==-1) {
ns6=false; mz7=true;
}
if (window.opera) { mz7=false; op5=true;}
else if (agt.indexOf('gecko')==-1) { mz7=false; exclude=true; }
}
if (agt.indexOf('opera 4')!=-1) { op5=false; mz7=false; exclude=true; }
var ns4=false;
if ((agt.indexOf('mozilla')!=-1)&&(parseInt(navigator.appVersion)>=4)&&!ie&&!op5&&!ns6&&!mz7) {
ns4=true; exclude=false;
}
if (agt.indexOf('webtv')!=-1) { ie=false; ie4=false; exclude=true; }
var win=true; var mac=false;
if (agt.indexOf('mac')!=-1) { win=false; mac=true; }
//djochange - added the following code to find Konqueror
if (navigator.vendor){
if (navigator.vendor =="KDE") {
ie=false;
ie4=false;
ie5=false;
konqi = true;
ns6=true;
ns4 = false;
exclude = false;

// exclude earlier than kde2.2
var thisKDE=agt;
var splitKDE=thisKDE.split("konqueror/");
var aKDE=splitKDE[1].split("; ");
var KDEn=parseFloat(aKDE[0]);
var oldKde=false;
if(KDEn<2.2){oldKde=true;exclude=true;ns6=false;konqi=false;}
}
}
//end djochange
// differentiate between opera 5 and 6
var op6=false;
if(op5){
if((agt.indexOf("opera 6")!=-1)||(agt.indexOf("opera/6")!=-1)){op6=true;op5=false;}
}
// find ie6
var ie6=false;
if(ie5&&agt.indexOf("msie 6")!=-1) { ie6=true; }


// array building functions for custom.js
var mu="mu";var m=0;var sm=0;var cm=0;var sp=0;
var mI=new Array;
var sP=new Array;
var sI=new Array;
var cP=new Array;
var cI=new Array;

function addMainItem(ma,mb,mc,md,me,mf,mg,mh,mi){
sm=0;
if(!mb||mb==""){mb="&nbsp;";}
while(mb.indexOf('<BR>')!=-1){mb=mb.replace('<BR>','<br>');}
if(!md||md==""){md="left";}
if(!me||me==""){me="_self";}
if(!mf||mf==""||altDISPLAY==""){mf="none";if(ie5){mf="";}}
if(!mg){mg=0;}if(!mh){mh=0;}
if(!mi||mi==""||mi=="c"||mi=="C"){mi="-";}
if(mi!="-"){mi=mi.toLowerCase();}
mI[m]=new Array(ma,mb,mc,md,me,mf,mg,mh,mi);
m++;
}

function defineSubmenuProperties(spa,spb,spc,spd,spe,spf){
if(!ie5){spa+=(sbSIZE*2); }
if(!spc||spc==""){spc="left";}
if(!spd){spd=0;}
if(!spe){spe=0;}
if(!spf){spf="";}
sP[(m-1)]=new Array(spa,spb,spc,spd,spe,spf);
if(sm==0){
sI[(m-1)]=new Array; cP[(m-1)]=new Array; cI[(m-1)]=new Array;
}
}

function addSubmenuItem(sma,smb,smc,smd){
cm=0;var sme=true;
if(sme&&sma=="~"){sme=false;sma="";smc="";}
if(!sma||sma==""){sma="#";}
if(!smb||smb==""){smb="&nbsp;";}
while(smb.indexOf('<BR>')!=-1){smb=smb.replace('<BR>','<br>');}
if(!smc||smc==""){smc="_self";}
if(!smd||smd==""||altDISPLAY==""){smd="none";if(ie5){smd="";}}
sI[(m-1)][sm]=new Array(sma,smb,smc,smd,sme);
sm++;
}

function defineChildmenuProperties(cpa,cpb,cpc,cpd,cpe,cpf){
if(!ie5){cpa+=(sbSIZE*2); }
if(!cpc||cpc==""){cpc="left";}
if(!cpd){cpd=0;}
if(!cpe){cpe=0;}
if(!cpf){cpf="";}
cP[(m-1)][(sm-1)]=new Array(cpa,cpb,cpc,cpd,cpe,cpf);
cI[(m-1)][(sm-1)]=new Array;
}

function addChildmenuItem(cma,cmb,cmc,cmd){
var cme=true;
if(cma&&cma=="~"){cme=false;cma="";cmc="";}
if(!cma||cma==""){cma="#";}
if(!cmb||cmb==""){cmb="&nbsp;";}
while(cmb.indexOf('<BR>')!=-1){cmb=cmb.replace('<BR>','<br>');}
if(!cmc||cmc==""){cmc="_self";}
if(!cmd||cmd==""||altDISPLAY==""){cmd="none";if(ie5){cmd="";}}
cI[(m-1)][(sm-1)][cm]=new Array(cma,cmb,cmc,cmd,cme);
cm++;
}
// custom upgrade backup definitions
var keepSubLIT=true;var chvOFFSET=0;var chhOFFSET=-5;var openTIMER=0;var closeTIMER=330;var cellCLICK=true;var aCURSOR="hand";
var altDISPLAY="";var allowRESIZE=true;var redGRID=false;var gridWIDTH=0;var gridHEIGHT=0;var documentWIDTH=0;

// custom window opening function
var nUrl,nW,nH;
var nWin=new Array;
var nw=0;
function openWindow(nUrl,nW,nH){
	nWin[nw] = open(nUrl, "","width="+nW+",height="+nH+",status=yes,scrollbars=no,scrolling=no,toolbar=no,menubar=no,location=no,resizable=yes");
	nw++;
	}

