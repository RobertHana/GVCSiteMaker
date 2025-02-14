//UDMv3.2.2


// filter for undefined arrays 
for (f=0;f<mI.length;f++){
if(!sP[f]){sP[f]=new Array(mI[f][2],mI[f][3],mI[f][3],0,0);}
if(!cP[f]){cP[f]=new Array;}
if(!sI[f]){sI[f]=new Array;}
if(!cI[f]){cI[f]=new Array;}
for (sf=0;sf<sI[f].length;sf++){
if(!cP[f][sf]){cP[f][sf]=new Array;}
if(!cI[f][sf]){cI[f][sf]=new Array;}
}}


// appreviations
var bh=baseHREF;
var ttt='<table border=0 cellpadding=0 cellspacing=';
var aL=absLEFT; aT=absTOP;

if(aT<0){aT=0;}
if(aL<0){aL=0;}
if(svPADDING<=0){svPADDING=1;}

// find the inner height of the browser window

var bHe=document.body.clientHeight;
if(mac){bHe-=15; if(ie5){bHe-=15;}}


// find background colors or images

var back_defs=new Array(mCOLOR,bCOLOR,rCOLOR,smCOLOR,sbCOLOR,srCOLOR,shCOLOR);
var useIMG=new Array(false,false,false,false,false,false);
var bks=new Array;
var mbackers=new Array;

for (b=0;b<back_defs.length;b++){
bks[b]='bgcolor='+back_defs[b];
if((back_defs[b].indexOf('.gif') != -1) || (back_defs[b].indexOf('.jpg') != -1) || back_defs[b]==''){useIMG[b]=true;}
if(useIMG[b]){
bks[b]='background="'+bh + back_defs[b]+'"'; 
}
if(back_defs[b]==''){bks[b]='';} 
}




// link color behaviour
if(aHOVER==""){aHOVER=aLINK;}
var aRoll=aLINK;
if(rCOLOR!=""){aRoll=aHOVER;}

if(saHOVER==""){saHOVER=saLINK;}
var saRoll=saLINK;
if(srCOLOR!=""){saRoll=saHOVER;}


// convert shadow type
var shy=false;
if ((typeof shSIZE)=="string") { 
shy=true;
shSIZE=Number(shSIZE); 
}



// find the inner width of the browser window

var nWid=document.body.clientWidth;
var bW=nWid;

//override width if defined
if(documentWIDTH>0){bW=documentWIDTH;}

// find the nav width and horizontal space

var eS=0;
var nS=0;
for (j=0;j<mI.length;j++){
if(mI[j][2]==""){mI[j][2]=10;}
if(mI[j][1]==""){mI[j][1]='&nbsp;'; 
}
nS+=mI[j][2]+bSIZE; 
}
eS=bW-nS-bSIZE;

if(menuALIGN!="free"&&(bW<nS)){menuALIGN="left"; aL=0; stretchMENU=false; showBORDERS=false;}



// find the nav height
var nH=fSIZE+5+vPADDING;
if (fSIZE<13) { nH+=(13-sfSIZE); }
var onH=nH;

// find wrapping links routine by brendan armstrong
var Mrows=new Array;var Nrows=new Array;
var thisNH=new Array;var thisAH=new Array;
for(i=0;i<mI.length;i++){
var thisT=mI[i][1];
var splitT=thisT.split('<br>');
Mrows[i]=splitT.length;
Nrows[i]=Mrows[i];
}

function cFun(a,b) { return b-a; }
Mrows.sort(cFun);
nH=nH*Mrows[0];

var aH=nH+(bSIZE*2); 


for(i=0;i<mI.length;i++){
thisNH[i]=nH;if(menuALIGN=="free"){thisNH[i]=onH*Nrows[i];}
thisAH[i]=nH+(bSIZE*2);if(menuALIGN=="free"){thisAH[i]=thisNH[i]+(2*bSIZE);}
}



// find the subnav item height

var snH=sfSIZE+5+svPADDING;
if (sfSIZE<13) { snH+=(13-sfSIZE); }
var asH=snH+(sbSIZE*2); 



// set values for different alignments
var free=false;
var xp=0;
var aP=new Array;
var aPL=new Array;

if(menuALIGN=="free"){
stretchMENU=false;
showBORDERS=false;
free=true;
for (xc=0;xc<mI.length;xc++){
aP[xc]=mI[xc][6]+aT;
aPL[xc]=mI[xc][7]+aL;
}
} else {
aP[xp]=aT;
}



var stA=0; var absR; var rL; 

var ra=false;
if(menuALIGN=="right"){
ra=true;
absR=aL; 
aL=eS-absR;
rL=aL;
if(rL<0){rL=0;}
aL=0;
}

var ca=false;
if(menuALIGN=="center"){
ca=true;
aL=eS / 2;
rL=aL;
if(rL<0){rL=0;}
if(mac){rL-=1;}
aL=0;
}

var la=false;
if(menuALIGN=="left"){
la=true;
absR=aL;
rL=aL; 
if(stretchMENU&&absR>0){stA=absR+(2*bSIZE);}
if(rL<0){rL=0;}
if(stretchMENU){aL=0;}
}

var sL=rL;
if(ra){sL-=absR;}
if(la){sL+=absR;}

// how many main nav items
var ntl=0;
for (intl=0;intl<mI.length;intl++){if(mI[intl][1]!=""){ntl++;}}

var cSt='cursor:'+aCURSOR;



// do nothing
function doNothing(){}



// store some object references
var oR=new Array;
for (obr=0;obr<14;obr++){oR[obr]=new Array;}

var obGrid;
function storeObjects(){
obMaster=d.all["master"]; 
obGrid=d.all["grid"]; 
for (sob=0;sob<mI.length;sob++){
oR[0][sob]=d.all['roll'+sob];
oR[1][sob]=d.all['subnav'+sob];
oR[2][sob]=d.all['sublinks'+sob];
oR[3][sob]=d.all['gridblocker'+sob];
oR[12][sob]=d.all['gridLblocker'+sob];
if(shCOLOR!=""){oR[4][sob]=d.all['shadow'+sob];}
oR[13][sob]=new Array;
for (sobr=5;sobr<12;sobr++){oR[sobr][sob]=new Array;}
for (soj=0;soj<sI[sob].length;soj++){
oR[5][sob][soj]=d.all['subroll'+sob+'-'+soj];
if(cP[sob][soj]){
if(cP[sob][soj].length>0){
oR[6][sob][soj]=d.all['subroll'+sob+'-'+soj];
if(shCOLOR!=""){oR[7][sob][soj]=d.all['childshadow'+sob+'-'+soj];}
if((chhOFFSET+cP[sob][soj][4])>0){oR[8][sob][soj]=d.all['childgrid'+sob+'-'+soj];}
oR[9][sob][soj]=d.all['childnav'+sob+'-'+soj];
oR[10][sob][soj]=d.all['childlinks'+sob+'-'+soj];
oR[11][sob][soj]=new Array;
oR[13][sob][soj]=new Array;
for (scj=0;scj<cI[sob][soj].length;scj++){
oR[11][sob][soj][scj]=d.all['childroll'+sob+'-'+soj+'-'+scj];
oR[13][sob][soj][scj]=d.all['childlinktext'+sob+'-'+soj+'-'+scj];
}
}
}
}
}
findHeights();
}


// code saving visibility toggles
function miH(hr){if(allready&&hr){hr.style.visibility="hidden";}}
function miV(hv){if(allready&&hv){hv.style.visibility="visible";}}



// clear submenus
var pId=0;
var pCId=0;
var rTimer;var rCount=0;

function aCM(n){
if(allready){
miH(obGrid);
miH(oR[3][pId]);
miH(oR[12][pId]);
miH(oR[4][pId]);
miH(oR[1][pId]); 
miH(oR[2][pId]); 
if(n!=pId||rCount>0){
for (sj=0;sj<sI[pId].length;sj++){
miH(oR[5][pId][pCId]);
}
clearTimeout(rTimer);rCount=0;
miH(oR[0][pId]); 
if(cP[pId][pCId]){
if(cP[pId][pCId].length>0){
miH(oR[5][pId][pCId]);
miH(oR[8][pId][pCId]);
miH(oR[9][pId][pCId]);
miH(oR[10][pId][pCId]);
if(oR[7][pId][pCId]){
oR[7][pId][pCId].filters.alpha.opacity=0;
miH(oR[7][pId][pCId]); 
}
}
}
}
usegrid=false;
}
}



// start menu closing timer
function cM(n){
if(allready){
var ieNum=n;
if(pCId==0&&!usegrid){
aCM(ieNum); 
pId=0;pCId=0;
} else {
if(rCount==0){
rCount++;
rTimer=setTimeout("cM(pId)",closeTIMER);
} else {
aCM(ieNum); 
pId=0;pCId=0;
}
}
}
}

// special closing for grid
var usegrid=false;
function gridClearMenus(){
if(allready){
rCount=0;clearTimeout(rTimer);
usegrid=true;
cM();
}
}




// open selected submenu
var rObj; var n; var sms=-1;
var gridOkay=false;
function oM(n){
if(allready){
sms=n;
miV(obGrid);
if(sI[n]!=""){
for (ohm=0;ohm<sI[n].length;ohm++){
miH(oR[5][n][ohm]); 
}
miV(oR[3][n]);
miV(oR[12][n]);
miV(oR[0][n]); 
miV(oR[1][n]); 
miV(oR[2][n]); 
if(sI[n]!=''){miV(oR[4][n]);}
}
pId=n;
rCount=0;clearTimeout(rTimer);
}
}


// clear child menus
function cCM(snum,cnum){
if(allready){
miH(oR[5][snum][pCId]); 
if(cP[snum][pCId]){
if(cP[snum][pCId].length>0&&cI[snum][pCId].length>0){
miH(oR[8][snum][pCId]);
miH(oR[9][snum][pCId]);
miH(oR[10][snum][pCId]);
if(oR[7][snum][pCId]){
oR[7][snum][pCId].filters.alpha.opacity=0;
miH(oR[7][snum][pCId]); 
}
}
}
pCId=0;
}
}


// open selected child menu
var snum; var cnum; var cmsS=-1; var cmsC=-1;
function oCM(snum,cnum){
if(allready){
cmsS=snum;cmsC=cnum;
if(cP[snum][cnum].length>0&&cI[snum][cnum].length>0){
miV(oR[8][snum][cnum]);
miV(oR[9][snum][cnum]);
miV(oR[10][snum][cnum]);
if(oR[7][snum][cnum]&&cI[snum][cnum]!=''){
oR[7][snum][cnum].filters.alpha.opacity=shOPACITY;
miV(oR[7][snum][cnum]); 
}
}
pCId=cnum;
rCount=0;clearTimeout(rTimer);
}
}



// td click handlers
function goToUrl(cUrl,cT){
if(cellCLICK||(cellCLICK==mu)){
if(cT=="_self"){self.document.location=cUrl;}
else if(cT=="_top"){top.document.location=cUrl;}
else if(cT=="_parent"){parent.document.location=cUrl;}
else if(cT=="_blank"){var newwin=open(cUrl,"","status=yes,scrollbars=yes,scrolling=yes,toolbar=yes,menubar=yes,location=yes,resizable=yes");}
else {if(top[cT]){top[cT].document.location=cUrl} else {document.location=cUrl;}}
}
}




//alert("assemble main nav");

var S='';
var T='';


// nav stretching
var stbSize=0;
if(showBORDERS){stbSize=bSIZE;}
if(stretchMENU||showBORDERS){
S+=ttt + stbSize+' '+bks[1]+' width='+bW+'  height='+aH+' id="stretchnav" ';
S+='style="z-index:'+(zORDER+1)+'\; position:absolute\; top:'+aP[xp]+'\; left:0\;">'; 
S+='<tr><td onmouseover="gridClearMenus()" onmousedown="aCM()"></td></tr></table>';
if(stretchMENU){
var tbf=bks[0]; if(showBORDERS){tbf='';}
var tbs=bks[0]; if(!showBORDERS){tbs='';}
S+=ttt + stbSize+' width='+bW+' '+tbf+' height='+aH+' style="z-index:'+(zORDER+2)+'\; position:absolute\; top:'+aP[xp]+'\; left:0\;"><tr><td '+tbs+' onmouseover="gridClearMenus()" onmousedown="aCM()"></td></tr></table>';
}
}


// event capturing layer
var evW=bW;if(mac&&ie5){evW=(bW-15);}
var GRw=evW;if(gridWIDTH>0){GRw=gridWIDTH;}
var GRh=bHe;if(gridHEIGHT>0){GRh=gridHEIGHT;}
var GRbc="";if(redGRID){GRbc="background-color:red";}
T+='<span id="grid" style="'+GRbc+'\; visibility:hidden\; position:absolute\; top:0\; left:0\; width:'+GRw+'\; height:'+GRh+'\; z-index:'+zORDER+'" onmousedown="aCM()" onmouseover="gridClearMenus()">&nbsp;</span>';



// open frame
if(!free){T+=ttt + bSIZE+' width='+nS+' height='+aH+' id="mainnav"  '+bks[1]+' style="z-index:'+(zORDER+4)+'\; position:absolute\; top:'+aP[xp]+'px\; left:'+rL+'px\;"><tr>';}



// links
var cSt=new Array;
var cTd=new Array;

var linkHover=' onmouseover="if(allready){this.style.color=\''+aHOVER+'\'}" onmouseout="if(allready){this.style.color=\''+aLINK+'\'}"';
if(aHOVER==''||rCOLOR!=''){linkHover='';}

var space=new Array;

// alt text arrays
var aTx=new Array;
var sTx=new Array;
var cTx=new Array;
var altNull='';

// write alt text to status bar 
function writeStatus(sTxt){if(allready){window.status=sTxt; return true;}}


for (i=0;i<mI.length;i++){


aTx[i]='';
if(mI[i][5]!="none"){
if(altDISPLAY=="title"){aTx[i]=' title="'+mI[i][5]+'" ';}
if(altDISPLAY=="status"){aTx[i]=' onmouseover="if(allready){return writeStatus(mI['+i+'][5])}" onmouseout="if(allready){return writeStatus(altNull)}" ';}
}

if(win&&ie4){
space[i]=' left:0px\;';
} else {
space[i]=' left:0px\;';
if(mI[i][3]=="left"){space[i]=' left:'+tINDENT+'px\;';}
if(mI[i][3]=="right"){space[i]=' left:-'+tINDENT+'px\;';}
}

if(mI[i][1]!=""){
if(mI[i][0]==""){ 
mI[i][0]="javascript:doNothing()"; 
cSt[i]='cursor:default'; 
cTd[i]='cursor:default'; 
} else {
cSt[i]='cursor:'+aCURSOR;
cTd[i]='cursor:default';
if(cellCLICK==true){cTd[i]='cursor:'+aCURSOR;}
}
if(free){T+=ttt + bSIZE+' width='+(mI[i][2]+(2*bSIZE))+' height='+thisAH[i]+' '+bks[1]+' style="z-index:'+(zORDER+4)+'\; position:absolute\; top:'+aP[i]+'px\; left:'+aPL[i]+'px\;"><tr>';}
var tmItem='href="'+mI[i][0]+'" target="'+mI[i][4]+'"';
if((cellCLICK||(cellCLICK==mu))&&(mI[i][4]=="_blank"||mI[i][0].indexOf("mailto:")!=-1)){
if(mac){tmItem='href="'+mI[i][0]+'" onclick="goToUrl(mI['+i+'][0],\'_blank\')\;return false" ';}
else {tmItem='href="'+mI[i][0]+'" onclick="return false" ';}
}
T+='<td onmouseover="if(allready){aCM()\; oR[0]['+i+'].style.visibility=\'visible\'\; oM('+i+')}" class=mTD style="height:'+thisNH[i]+'px\;" '+bks[0]+'>'+ttt+'0 width='+mI[i][2]+' style="z-index:'+(zORDER+4)+'\; height:'+thisNH[i]+'\;"><tr><td align="'+mI[i][3]+'" onclick="goToUrl(mI['+i+'][0],mI['+i+'][4])" style="'+cTd[i]+'" '+aTx[i]+'><a '+tmItem+' id="mainlinktext'+i+'" style="'+cSt[i]+'\; position:relative\; top:'+(vtOFFSET)+'\; '+space[i]+'" '+linkHover+'>'+mI[i][1]+'</a></td></tr></table></td>'; 
if(free){T+='</tr></table>';}
}
}

// close frame
if(!free){T+='</tr></table>';}



// rollover cells 
var rollLeft=rL+bSIZE;

for (i=0;i<mI.length;i++){
if(free){rollLeft=mI[i][7]+bSIZE+aL; xp=i;}
if(mI[i][1]!=""){
var tmItem='href="'+mI[i][0]+'" target="'+mI[i][4]+'"';
if((cellCLICK||(cellCLICK==mu))&&(mI[i][4]=="_blank")||(mI[i][0].indexOf("mailto:")!=-1)){
if(mac){tmItem='href="'+mI[i][0]+'" onclick="goToUrl(mI['+i+'][0],\'_blank\')\;return false" ';}
else {tmItem='href="'+mI[i][0]+'" onclick="return false" ';}
}
T+=ttt+'0 id="roll'+i+'" style="visibility:hidden\; z-index:'+(zORDER+5)+'\; height:'+thisNH[i]+'\; position:absolute\; top:'+(aP[xp]+bSIZE)+'px\; left:'+rollLeft+'px\;" onmouseover="if(allready){cM('+i+')\; oM('+i+')\; this.style.visibility=\'visible\'}" onmouseout="if(allready&&!keepLIT){miH(this)}"><tr><td class=mTD '+bks[2]+'>'+ttt+'0 width='+mI[i][2]+' style="height:'+thisNH[i]+'\;"><tr><td align="'+mI[i][3]+'" onclick="goToUrl(mI['+i+'][0],mI['+i+'][4])" style="'+cTd[i]+'" '+aTx[i]+'><a '+tmItem+' id="mainrolltext'+i+'" style="'+cSt[i]+'\; position:relative\; top:'+(vtOFFSET)+'\; '+space[i]+'\; color:'+aRoll+'\; " '+linkHover+'>'+mI[i][1]+'</a></td></tr></table></td></tr></table>';
}
rollLeft+=(mI[i][2]+bSIZE);
}
xp=0;


//alert("assemble submenus");

var M=''; var C='';

var SUBaL=0;



for (c=0;c<mI.length;c++){

sTx[c]=new Array;
cTx[c]=new Array;




// text alignment and indentation
var stAlign=sP[c][2];
if(win&&ie4){
var space=' left:0px\;';
} else {
var space=' left:0px\;';
if(stAlign=="left"){space=' left:'+stINDENT+'px\;';}
if(stAlign=="right"){space=' left:-'+stINDENT+'px\;';}
}




// find next submenu position
if(!free){
if(c==0){
SUBaL=rL+bSIZE;
}
else {SUBaL+=mI[(c-1)][2]+bSIZE;}
} else {
SUBaL=aPL[c];
xp=c;
}

// specify edge alignment
var acL=SUBaL+hOFFSET+sP[c][4];
if(sP[c][1]=="right"){
acL=SUBaL-(sP[c][0]-mI[c][2])-hOFFSET-sP[c][4];
}

// grid block layer for vertical offset
if((vOFFSET+sP[c][3])>0){M+='<span id="gridblocker'+c+'" style="width:'+sP[c][0]+'px\; height:'+(vOFFSET+sP[c][3])+'px\; visibility:hidden\; z-index:'+(zORDER+6)+'\;position:absolute\; top:'+(aP[xp]+thisAH[c])+'px\; left:'+acL+'px\;" onmouseover="rCount=0\;clearTimeout(rTimer)\;"></span>';}

// grid block layer for horizontal offset
var gblw=acL-mI[c][2]-SUBaL; 
var gbposL=(SUBaL+mI[c][2]+(2*bSIZE));
if(sP[c][1]=="right"){
gblw=mI[c][7]+bSIZE+aL-sP[c][0]-acL; 
gbposL=(mI[c][7]+bSIZE+aL-gblw);
}

if(gblw>0){M+='<span id="gridLblocker'+c+'" style="width:'+gblw+'px\; height:10px\; visibility:hidden\; z-index:'+(zORDER+6)+'\;position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3])+'px\; left:'+gbposL+'px\;" onmouseover="rCount=0\;clearTimeout(rTimer)\;"></span>';}


// container cells
M+=ttt + sbSIZE+' id="subnav'+c+'" width="'+sP[c][0]+'" '+bks[4]+' style="visibility:hidden\; z-index:'+(zORDER+7)+'\; position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3])+'px\; left:'+acL+'px\;">'; 
var SUBaT=aP[xp]+thisAH[c]+vOFFSET;
for (i=0;i<sI[c].length;i++){
if(sI[c][i][0]!=''&&sI[c][i][1]!=''){

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=sI[c][i][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

M+='<tr><td class=SUBmTD '+bks[3]+' align="'+stAlign+'" width="'+(sP[c][0]-(sbSIZE*2))+'" style="height:'+thisH+'px\;"><span style="width:1px\; height:'+thisH+'px\;"></span></td></tr>';
}
}
M+='</table>';

// links
var sublinkHover=' onmouseover="this.style.color=\''+saHOVER+'\'" onmouseout="this.style.color=\''+saLINK+'\'"';
if(saHOVER==''||srCOLOR!=''){sublinkHover='';}
M+=ttt + sbSIZE+' width="'+sP[c][0]+'" id="sublinks'+c+'" style="visibility:hidden\; z-index:'+(zORDER+8)+'\; position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3])+'px\; left:'+acL+'px\;">'; 
var SUBaT=aP[xp]+aH+vOFFSET+sP[c][3];
var scSt=new Array;
var scTd=new Array;
for (i=0;i<sI[c].length;i++){

cTx[c][i]=new Array;
sTx[c][i]='';
if(sI[c][i][3]!="none"){
if(altDISPLAY=="title"){sTx[c][i]=' title="'+sI[c][i][3]+'" ';}
if(altDISPLAY=="status"){sTx[c][i]=' onmouseover="return writeStatus(sI['+c+']['+i+'][3])" onmouseout="return writeStatus(altNull)" ';}
}


if(sI[c][i][0]=="#"){
sI[c][i][0]="javascript:doNothing()"; 
scSt[i]='cursor:default'; 
scTd[i]='cursor:default';
} else {
scSt[i]='cursor:'+aCURSOR;
scTd[i]='cursor:default';
if(cellCLICK==true){scTd[i]='cursor:'+aCURSOR;}
}
var tmItem='href="'+sI[c][i][0]+'" target="'+sI[c][i][2]+'"';
if((cellCLICK||(cellCLICK==mu))&&(sI[c][i][2]=="_blank"||sI[c][i][0].indexOf("mailto:")!=-1)){
if(mac){tmItem='href="'+sI[c][i][0]+'" onclick="goToUrl(sI['+c+']['+i+'][0],\'_blank\')\;return false" ';}
else {tmItem='href="'+sI[c][i][0]+'" onclick="return false" ';}
}

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=sI[c][i][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

M+='<tr><td class=SUBmTD height='+snH+' onmouseover="rCount=0\;clearTimeout(rTimer)\; cCM('+c+','+i+')\; oCM('+c+','+i+')\; if(sI['+c+']['+i+'][4]){oR[5]['+c+']['+i+'].style.visibility=\'visible\'}"  onmouseout="if(!keepSubLIT){oR[5]['+c+']['+i+'].style.visibility=\'hidden\'\;}">'+ttt+'0 id="sublink'+c +'-'+i+'" width='+(sP[c][0]-(sbSIZE*2))+' style="height:'+thisH+'"><tr><td align="'+stAlign+'" onclick="goToUrl(sI['+c+']['+i+'][0],sI['+c+']['+i+'][2])" style="'+scTd[i]+'" '+sTx[c][i]+'>';
M+='<a '+tmItem+' id="linktext'+c+'-'+i+'"  onfocus="storeFocusId('+c+','+i+',-1,false)\; rCount=0\;clearTimeout(rTimer)\; cCM('+c+','+i+')\; oCM('+c+','+i+')\; if(sI['+c+']['+i+'][4]){oR[5]['+c+']['+i+'].style.visibility=\'visible\'}"  onblur="if(!keepSubLIT){oR[5]['+c+']['+i+'].style.visibility=\'hidden\'\;}" style="'+scSt[i]+'\; position:relative\;top:'+(svtOFFSET)+'\; '+space+'" '+sublinkHover+'>'+ sI[c][i][1]+'</a>';
M+='</td></tr></table></td></tr>';
}
M+='</table>';

// subrollover cells
var subrollTop=aP[xp]+thisAH[c]+vOFFSET+sP[c][3]+sbSIZE;
var chTop=new Array;
for (i=0;i<sI[c].length;i++){
var tmItem='href="'+sI[c][i][0]+'" target="'+sI[c][i][2]+'"';
if((cellCLICK||(cellCLICK==mu))&&(sI[c][i][2]=="_blank"||sI[c][i][0].indexOf("mailto:")!=-1)){
if(mac){tmItem='href="'+sI[c][i][0]+'" onclick="goToUrl(sI['+c+']['+i+'][0],\'_blank\')\;return false" ';}
else {tmItem='href="'+sI[c][i][0]+'" onclick="return false" ';}
}

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=sI[c][i][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

T+='<table cellpadding=0 cellspacing=0 border=0 id="subroll'+c+'-'+i+'" width='+(sP[c][0]-(sbSIZE*2))+' '+bks[5]+' onmouseover="rCount=0\;clearTimeout(rTimer)\; this.style.visibility=\'visible\'" onmouseout="if(!keepSubLIT){miH(this)}" style="visibility:hidden\; z-index:'+(zORDER+9)+'\; height:'+thisH+'\; position:absolute\; top:'+subrollTop+'px\; left:'+(acL+sbSIZE)+'px\;"><tr><td align="'+stAlign+'" class=SUBmTD onclick="goToUrl(sI['+c+']['+i+'][0],sI['+c+']['+i+'][2])" style="'+scTd[i]+'" '+sTx[c][i]+'><a '+tmItem+' id="rolltext'+c+'-'+i+'" style="'+scSt[i]+'\; position:relative\; top:'+(svtOFFSET)+'\; '+space+'\; color:'+saRoll+'\;" '+sublinkHover+'>'+ sI[c][i][1]+'</a></td></tr></table>';
chTop[i]=subrollTop;
subrollTop+=thisH+sbSIZE;
}


//alert("drop shadow"); 
var shb=0;if(shy){shb=(2*shSIZE);}
if(shCOLOR!=""){M+=ttt+'0 '+bks[6]+' id="shadow'+c+'" style="visibility:hidden\; filter:alpha(opacity='+shOPACITY+')\; z-index:'+(zORDER+6)+'\; position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3]+shSIZE-shb)+'px\; left:'+(acL+shSIZE-shb)+'px\; width:'+(sP[c][0]+shb)+'\; height:1\;"><tr><td>&nbsp;</td></tr></table>';}


//alert("assemble child menus");
for (sq=0;sq<sI[c].length;sq++){
if(cP[c][sq].length>0&&cI[c][sq].length>0){

// specify edge alignment
var chL;
if(cP[c][sq][1]=="right"){
chL=acL-cP[c][sq][0]-chhOFFSET-cP[c][sq][4];
cgL=chL+cP[c][sq][0];
} else {
chL=acL + sP[c][0]+chhOFFSET+cP[c][sq][4];
cgL=(chL-(chhOFFSET+cP[c][sq][4]));
}


// grid block layer for horizontal offset
if((chhOFFSET+cP[c][sq][4])>0){
C+='<span id="childgrid'+c+'-'+sq+'" style="width:'+(chhOFFSET+cP[c][sq][4])+'px\; height:10px\; visibility:hidden\; z-index:'+(zORDER+9)+'\;position:absolute\; top:'+((chTop[sq]-sbSIZE+chvOFFSET)+cP[c][sq][3])+'px\; left:'+cgL+'px\;" onmouseover="rCount=0\;clearTimeout(rTimer)\;"></span>'; 
}

// container cells
C+=ttt + sbSIZE+' id="childnav'+c+'-'+sq+'" '+bks[4]+' style="visibility:hidden\; z-index:'+(zORDER+10)+'\; position:absolute\; top:'+((chTop[sq]-sbSIZE+chvOFFSET)+cP[c][sq][3])+'px\; left:'+chL+'px\; width:'+cP[c][sq][0]+'px\; height:'+(((snH+sbSIZE)*cI[c][sq].length)+sbSIZE)+'px\;">';
for (cj=0;cj<cI[c][sq].length;cj++){
if(cI[c][sq].length>0){

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=cI[c][sq][cj][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

C+='<tr><td class=SUBmTD '+bks[3]+' align="'+stAlign+'" width="'+(cP[c][sq][0]-(sbSIZE*2))+'" style="height:'+thisH+'px\;"><span style="width:1px\; height:'+thisH+'px\;"></span></td></tr>';
}
}
C+='</table>';
}

// text alignment and indentation
var ctAlign=cP[c][sq][2];

if(win&&ie4){
var cspace=' left:0px\;';
} else {
var cspace=' left:0px\;';
if(ctAlign=="left"){cspace=' left:'+stINDENT+'px\;';}
if(ctAlign=="right"){cspace=' left:-'+stINDENT+'px\;';}
}

var ccTd='cursor:default';
if(cellCLICK==true){ccTd='cursor:'+aCURSOR;}
var ccAl='cursor:'+aCURSOR;

// links
C+=ttt + sbSIZE+' id="childlinks'+c+'-'+sq+'" width="'+cP[c][sq][0]+'" style="visibility:hidden\; z-index:'+(zORDER+11)+'\; position:absolute\; top:'+((chTop[sq]-sbSIZE+chvOFFSET)+cP[c][sq][3])+'px\; left:'+chL+'px\;">'; 
for (cj=0;cj<cI[c][sq].length;cj++){

cTx[c][sq][cj]='';
if(cI[c][sq][cj][3]!="none"){
if(altDISPLAY=="title"){cTx[c][sq][cj]=' title="'+cI[c][sq][cj][3]+'" ';}
if(altDISPLAY=="status"){cTx[c][sq][cj]=' onmouseover="return writeStatus(cI['+c+']['+sq+']['+cj+'][3])" onmouseout="return writeStatus(altNull)" ';}
}

if(cI[c][sq][cj][0]=="#"){cI[c][sq][cj][0]="javascript:doNothing()";}
var tmItem='href="'+cI[c][sq][cj][0]+'" target="'+cI[c][sq][cj][2]+'"';
if((cellCLICK||(cellCLICK==mu))&&(cI[c][sq][cj][2]=="_blank"||cI[c][sq][cj][0].indexOf("mailto:")!=-1)){
if(mac){tmItem='href="'+cI[c][sq][cj][0]+'" onclick="goToUrl(cI['+c+']['+sq+']['+cj+'][0],\'_blank\')\;return false" ';}
else {tmItem='href="' +cI[c][sq][cj][0]+'" onclick="return false" ';}
}

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=cI[c][sq][cj][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

var ccTd='cursor:default';
if(cellCLICK==true){ccTd='cursor:'+aCURSOR;}
var ccAl='cursor:'+aCURSOR;
if(!cI[c][sq][cj][4]){ccAl='cursor:default';}
if(!cI[c][sq][cj][4]){ccTd='cursor:default';}
C+='<tr><td class=SUBmTD height='+snH+' onmouseover="rCount=0\;clearTimeout(rTimer)\; if(cI['+c+']['+sq+']['+cj+'][4]){oR[11]['+c+']['+sq+']['+cj+'].style.visibility=\'visible\'}" onmouseout="oR[11]['+c+']['+sq+']['+cj+'].style.visibility=\'hidden\'">'+ttt+'0 id="childlink'+c+'-'+sq+'-'+cj+'" width='+(cP[c][sq][0]-(sbSIZE*2))+' style="height:'+thisH+'"><tr><td align="'+ctAlign+'" onclick="goToUrl(cI['+c+']['+sq+']['+cj+'][0],cI['+c+']['+sq+']['+cj+'][2])" style="'+ccTd+'" '+cTx[c][sq][cj]+'><a '+tmItem+' id="childlinktext'+c+'-'+sq+'-'+cj+'" onfocus="storeFocusId('+c+','+sq+','+cj+',true)\; rCount=0\;clearTimeout(rTimer)\; if(cI['+c+']['+sq+']['+cj+'][4]){oR[11]['+c+']['+sq+']['+cj+'].style.visibility=\'visible\'}" onblur="oR[11]['+c+']['+sq+']['+cj+'].style.visibility=\'hidden\'" style="'+ccAl+'\; position:relative\;top:'+(svtOFFSET)+'\; '+cspace+'" '+sublinkHover+'>'+ cI[c][sq][cj][1]+'</a></td></tr></table></td></tr>';
}
C+='</table>';

// child rollover cells
var childrollTop=chTop[sq]+chvOFFSET+cP[c][sq][3];
for (cj=0;cj<cI[c][sq].length;cj++){
var tmItem='href="' +cI[c][sq][cj][0]+'" target="' +cI[c][sq][cj][2]+'" ';
if((cellCLICK||(cellCLICK==mu))&&(cI[c][sq][cj][2]=="_blank"||cI[c][sq][cj][0].indexOf("mailto:")!=-1)){
if(mac){tmItem='href="'+cI[c][sq][cj][0]+'" onclick="goToUrl(cI['+c+']['+sq+']['+cj+'][0],\'_blank\')\;return false" ';}
else {tmItem='href="' +cI[c][sq][cj][0]+'" onclick="return false" ';}
}

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=cI[c][sq][cj][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

C+=ttt+'0 id="childroll'+c+'-'+sq+'-'+cj+'" onmouseover="rCount=0\;clearTimeout(rTimer)\; this.style.visibility=\'visible\'" onmouseout="miH(this)" '+bks[5]+' width='+(cP[c][sq][0]-(sbSIZE*2))+' style="visibility:hidden\; z-index:'+(zORDER+12)+'\; height:'+thisH+'\; position:absolute\; top:'+childrollTop+'px\; left:'+(chL+sbSIZE)+'px\;"><tr><td align="'+ctAlign+'" class=SUBmTD onclick="goToUrl(cI['+c+']['+sq+']['+cj+'][0],cI['+c+']['+sq+']['+cj+'][2])" style="'+ccTd+'" '+cTx[c][sq][cj]+'><a '+tmItem+' id="childrolltext'+c+'-'+sq+'-'+cj+'" style="'+ccAl+'\; position:relative\; top:'+(svtOFFSET)+'\; '+cspace+'\; color:'+saRoll+'\;" '+sublinkHover+'>'+ cI[c][sq][cj][1]+'</a></td></tr></table>';
childrollTop+=thisH+sbSIZE;
}
//alert("child drop shadow"); 
if(shCOLOR!=""&&(cP[c][sq].length>0&&cI[c][sq].length>0)){C+=ttt+'0 '+bks[6]+' id="childshadow'+c+'-'+sq+'" style="visibility:hidden\; filter:alpha(opacity='+shOPACITY+')\; z-index:'+(zORDER+9)+'\; position:absolute\; top:'+((chTop[sq]-sbSIZE+chvOFFSET)+shSIZE+cP[c][sq][3]-shb)+'px\; left:'+(chL+shSIZE-shb)+'px\; width:'+(cP[c][sq][0]+shb)+'\; height:1\;"><tr><td>&nbsp;</td></tr></table>';}


}
}





// static positioning layer
if (win) { d.write('<div id="master" style="position:absolute\; top:0px\; left:0px\; z-index:'+zORDER+'">'); }


// draw stretched layer
if(!free){d.write(S);}


// draw main nav 
d.write(T);


// draw submenus
d.write(M);


// draw child menus
d.write(C);


// close static positioning layer
if (win) { d.write('</div>'); }




// resize / reload trap

if(allowRESIZE==true){window.onresize=new Function("window.location.reload()");}



var sTimer;
function makeStatic(){
d.all["master"].style.pixelTop=d.body.scrollTop; 
sTimer=setTimeout("makeStatic()",0); 
}



// find shadow heights

var allready=false;
var shb=0;if(shy){shb=(2*shSIZE);}
function findHeights(){
for(h=0;h<mI.length;h++){
if(oR[4][h]){oR[4][h].style.height=oR[1][h].offsetHeight+shb;}
if(oR[12][h]){oR[12][h].style.height=oR[1][h].offsetHeight+shb+shSIZE;}
for (jh=0;jh<sI[h].length;jh++){
if(cP[h][jh].length>0&&cI[h][jh].length>0){
if(oR[7][h][jh]){oR[7][h][jh].style.height=oR[9][h][jh].offsetHeight+shb;}
if(oR[8][h][jh]){oR[8][h][jh].style.height=oR[9][h][jh].offsetHeight+shb+shSIZE;}
}
}
}
if(win&&ie4){adjustLinks();}
genericOnloadFunction();
if(staticMENU&&ie5){makeStatic();}
allready=true;
}




//adjust links for windows ie4
if(win&&ie4){
function shuntLink(shuntLink,shuntRoll,shuntX){
	if(shuntLink){shuntLink.style.left=shuntX;}
	if(shuntRoll){shuntRoll.style.left=shuntX;}
	}
function adjustLinks(){

for(i=0;i<mI.length;i++){
	var mlObj=d.all["mainlinktext"+i];
	var mrObj=d.all["mainrolltext"+i];
	//alert(mI[i][2]+"\n"+mlObj.offsetWidth);
	if(mI[i][3]=="left"){shuntLink(mlObj,mrObj,tINDENT);}
	if(mI[i][3]=="center"){shuntLink(mlObj,mrObj,parseInt((mI[i][2]-mlObj.offsetWidth)/2));}
	if(mI[i][3]=="right"){shuntLink(mlObj,mrObj,((mI[i][2]-mlObj.offsetWidth)-tINDENT));}
	for(c=0;c<sI[i].length;c++){
		var slObj=d.all["linktext"+i+"-"+c];
		var srObj=d.all["rolltext"+i+"-"+c];
		if(sP[i][2]=="left"){shuntLink(slObj,srObj,stINDENT);}
		if(sP[i][2]=="center"){shuntLink(slObj,srObj,parseInt((sP[i][0]-slObj.offsetWidth)/2));}
		if(sP[i][2]=="right"){shuntLink(slObj,srObj,((sP[i][0]-slObj.offsetWidth)-stINDENT));}
		if(cP[i][c]&&cP[i][c].length>0){
		for(j=0;j<cI[i][c].length;j++){
			var clObj=d.all["childlinktext"+i+"-"+c+"-"+j];
			var crObj=d.all["childrolltext"+i+"-"+c+"-"+j];
			if(cP[i][c][2]=="left"){shuntLink(clObj,crObj,stINDENT);}
			if(cP[i][c][2]=="center"){shuntLink(clObj,crObj,parseInt((cP[i][c][0]-clObj.offsetWidth)/2));}
			if(cP[i][c][2]=="right"){shuntLink(clObj,crObj,((cP[i][c][0]-clObj.offsetWidth)-stINDENT));}
			}
		}
		}
	}
}
}






// store objects
window.onload=storeObjects;


document.onclick=aCM;
