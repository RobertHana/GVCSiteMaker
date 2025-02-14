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
}
}



// differentiate between ns6.1 and earlier versions and between mz0.6 and later versions
var ns61=true;
if (ns6&&(agt.indexOf("6.0")!=-1)) { ns61=false; };
var mz6=false;
if (mz7&&(agt.indexOf("0.6")!=-1)) { mz6=true; }

//djochange
//compensates for unconstrained nav stretching
if (konqi == true) {
    mz6 = true;
    ns61 = false;
}
//end djochange

// appreviations
var bh=baseHREF;
var ttt='<table border=0 cellpadding=0 cellspacing=';
var aL=absLEFT; aT=absTOP;

if(aT<0){aT=0;}
if(aL<0){aL=0;}


// convert shadow type
var shy=false;
if ((typeof shSIZE)=="string") { 
shy=true;
shSIZE=Number(shSIZE); 
}



// find the inner width and height of the browser window
var bW=window.innerWidth; 
var bHe=window.innerHeight; 

// compensate for lack of scrollbar if applicable
//djochange added the if loop checking for Konqi. Konqi doesn't need to be adjusted
if (!konqi&&!op6) {
if(mz7){
if(document.width < (innerWidth-17)){bW-=17;}
if(menuALIGN=="free"){bW-=17;}
} else if(ns6){
if(document.width <= (innerWidth-30)){bW-=14;}
if(menuALIGN=="free"){bW-=14;}
}
}
//end djochange

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


// find the subnav item height

var snH=sfSIZE+5+svPADDING;
if (sfSIZE<13) { snH+=(13-sfSIZE); }
var asH=snH+(sbSIZE*2); 


for(i=0;i<mI.length;i++){
thisNH[i]=nH;if(menuALIGN=="free"){thisNH[i]=onH*Nrows[i];}
thisAH[i]=nH+(bSIZE*2);if(menuALIGN=="free"){thisAH[i]=thisNH[i]+(2*bSIZE);}
}



// set values for different alignments
var free=false;
var xp=0;
var aP=new Array;
var aPL=new Array;

if(menuALIGN=="free"){
free=true;
for (xc=0;xc<mI.length;xc++){
aP[xc]=mI[xc][6]+aT;
aPL[xc]=mI[xc][7]+aL;
}
} else {
aP[xp]=aT;
}


var stAdjust=0; var abR; var rL; 

var ralign=false;
if(menuALIGN=="right"){
ralign=true;
abR=aL; 
aL=eS-abR;
rL=aL;
if(rL<0){rL=0;}
aL=0;
}

var calign=false;
if(menuALIGN=="center"){
calign=true;
aL=parseInt(eS/2);
rL=aL;
if(rL<0){rL=0;}
aL=0;
}

var lalign=false;
if(menuALIGN=="left"){
lalign=true;
abR=aL;
rL=aL; 
if((op5||stretchMENU)&&abR>0){stAdjust=abR+(2*bSIZE);}
if(rL<0){rL=0;}
if(stretchMENU){aL=0;}
}

var subLEFT=rL;
if(ralign){subLEFT-=abR;}
if(lalign){subLEFT+=abR;}

// how many main nav items
var ntl=0;
for (intl=0;intl<mI.length;intl++){if(mI[intl][1]!=""){ntl++;}}

var cStyle='cursor:hand';



// do nothing
function doNothing(){}


// find background colors or images

var back_defs=new Array(mCOLOR,bCOLOR,rCOLOR,smCOLOR,sbCOLOR,srCOLOR);
var useIMG=new Array(false,false,false,false,false,false)
var bks=new Array;

for (b=0;b<6;b++){
bks[b]='bgcolor='+back_defs[b];
if((back_defs[b].indexOf('.gif') != -1) || (back_defs[b].indexOf('.jpg') != -1) || back_defs[b]==''){useIMG[b]=true;}
if(useIMG[b]){
bks[b]='background="'+bh + back_defs[b]+'"'; 
}
if(back_defs[b]==''){bks[b]='';} 
}


// convert shadow opacity
var mzOpac=100; 
if (ns61) { mzOpac=Math.round(shOPACITY/10) / 10; }


// store some object references
var oR=new Array;
for (obr=0;obr<17;obr++){oR[obr]=new Array;}

function storeObjects(){
obGrid=document.getElementById("grid"); 
for (sob=0;sob<mI.length;sob++){
oR[0][sob]=d.getElementById('roll'+sob);
oR[1][sob]=d.getElementById('subnav'+sob);
oR[2][sob]=d.getElementById('sublinks'+sob);
if((vOFFSET+sP[sob][3])>0){oR[3][sob]=d.getElementById('gridblocker'+sob);}
if(d.getElementById('gridLblocker'+sob)){oR[12][sob]=d.getElementById('gridLblocker'+sob);}
if(shCOLOR!=""){oR[4][sob]=d.getElementById('shadow'+sob);}
for (sobr=5;sobr<17;sobr++){oR[sobr][sob]=new Array;}
for (soj=0;soj<sI[sob].length;soj++){
oR[5][sob][soj]=d.getElementById('subroll'+sob+'-'+soj);

if(op6){
oR[13][sob][soj]=d.getElementById('sublink'+sob+'-'+soj);
oR[14][sob][soj]=d.getElementById('srolllink'+sob+'-'+soj);
var ltPos=oR[13][sob][soj].offsetTop;
var rtPos=oR[14][sob][soj].offsetTop;
oR[14][sob][soj].style.top=0-(rtPos-ltPos);
}

if(cP[sob][soj]){
if(cP[sob][soj].length>0){
oR[6][sob][soj]=d.getElementById('subroll'+sob+'-'+soj);
if(shCOLOR!=""){oR[7][sob][soj]=d.getElementById('childshadow'+sob+'-'+soj);}
if((chhOFFSET+cP[sob][soj][4])>0){oR[8][sob][soj]=d.getElementById('childgrid'+sob+'-'+soj);}
oR[9][sob][soj]=d.getElementById('childnav'+sob+'-'+soj);
oR[10][sob][soj]=d.getElementById('childlinks'+sob+'-'+soj);
oR[11][sob][soj]=new Array;
oR[15][sob][soj]=new Array;
oR[16][sob][soj]=new Array;
for (scj=0;scj<cI[sob][soj].length;scj++){
oR[11][sob][soj][scj]=d.getElementById('childroll'+sob+'-'+soj+'-'+scj);

if(op6){
oR[15][sob][soj][scj]=d.getElementById('childlink'+sob+'-'+soj+'-'+scj);
oR[16][sob][soj][scj]=d.getElementById('crolllink'+sob+'-'+soj+'-'+scj);
var ltPos=oR[15][sob][soj][scj].offsetTop;
var rtPos=oR[16][sob][soj][scj].offsetTop;
oR[16][sob][soj][scj].style.top=0-(rtPos-ltPos);
}

}
}
}
}
}
genericOnloadFunction();
}



// code saving visibility toggles
function miH(hr){if(hr&&hr.style){hr.style.visibility="hidden";}}
function miV(hv){if(hv&&hv.style){hv.style.visibility="visible";}}



// clear submenus
var pId=-1;
var pCId=-1;
var rTimer=0;var rCount=0;
function aCM(n){
if(pId>-1){
if(rTimer!=0){clearTimeout(rTimer); rTimer=0; rCount=0;}
miH(oR[0][pId]);
miH(oR[3][pId]);
miH(oR[12][pId]);
miH(oR[1][pId]); 
miH(oR[2][pId]); 
miH(oR[4][pId]); 
if(pCId>-1){
miH(oR[5][pId][pCId]);
if(cP[pId][pCId]){
if(cP[pId][pCId].length>0){
for (cjc=0;cjc<cI[pId][pCId].length;cjc++){
miH(oR[11][pId][pCId][cj]);
}
miH(oR[9][pId][pCId]);
miH(oR[10][pId][pCId]);
miH(oR[7][pId][pCId]); 
miH(oR[8][pId][pCId]);
}
}
}
miH(obGrid);
}
pId=-1; pCId=-1;
usegrid=false;
}






// start menu closing timer
function cM(n){
var nsNum=n;
if(pCId==-1&&!usegrid){
aCM(nsNum); 
} else {
if(rCount==0){
rCount++;
rTimer=setTimeout("cM(pId)",closeTIMER);
} else {
aCM(nsNum); 
pId=-1;pCId=-1;
}
}
}



// special closing for grid
var usegrid=false;
function gridClearMenus(){
usegrid=true;
cM();
}




// rollover and find menu
var n;
var gridOkay=false;
function oM(n){
miV(oR[0][n]);
if(sI[n]!=""){
if(sI[n]!=''){
miV(oR[4][n]); 
}
miV(oR[1][n]); 
miV(oR[2][n]); 
miV(oR[3][n]);
miV(oR[12][n]);
if(pCId>-1){
miH(oR[5][n][pCId]); 
}
}
miV(obGrid);
pId=n;
}


// clear child menus
function cCM(snum,cnum){
if(pCId>-1){
miH(oR[5][snum][pCId]);
if(cP[snum][pCId].length>0&&cI[snum][pCId].length>0){
miH(oR[9][snum][pCId]);
miH(oR[10][snum][pCId]);
miH(oR[7][snum][pCId]); 
miH(oR[8][snum][pCId]);
}

pCId=-1;
}
}



// actually open menu
function actuallyOpenChild(snum,cnum){
miV(oR[7][snum][cnum]); 
miV(oR[9][snum][cnum]);
miV(oR[10][snum][cnum]);
if((chhOFFSET+cP[snum][cnum][4])>0){
miV(oR[8][snum][cnum]);
}
}



// rollover and find child menu
var snum; var cnum;
function oCM(snum,cnum){
var ntS=snum; var ntC=cnum;
if(sI[snum][cnum][4]){miV(oR[5][snum][cnum]);}
if(cP[snum][cnum].length>0&&cI[snum][cnum].length>0){
actuallyOpenChild(ntS,ntC);
}
pCId=cnum;
}







// td click handlers
var linkClicked=false;
function goToUrl(cUrl,cTarg){
if(cellCLICK||(cellCLICK==mu)){
if(op6&&linkClicked==true){return false;}
if(cTarg=="_self"){self.document.location=cUrl;}
else if(cTarg=="_top"){top.document.location=cUrl;}
else if(cTarg=="_parent"){parent.document.location=cUrl;}
else if(cTarg=="_blank"){var newwin=open(cUrl)}
else {if(top[cTarg]){top[cTarg].document.location=cUrl} else {document.location=cUrl;}}
}
}





//alert("assemble main nav");

var T='';
var S='';
var spbk='';
if(bCOLOR!=""){
spbk=' background:'+bCOLOR+'\; '
if(useIMG[1]){spbk=' background-image:url('+ bh + bCOLOR+')\; ';}
}

// nav stretching and event capturing layer
var stbSize=bSIZE;
if(stretchMENU||showBORDERS){
S+='<span id="stretchnav" ';
var spbb='';
if(showBORDERS){spbb=spbk;} else {stbSize=0;}
S+='style="'+spbb+' z-index:'+(zORDER+1)+'\; position:absolute\; top:'+aP[xp]+'\; left:0\;">'; 
var stretchLayer='';

if(stretchMENU){stretchLayer=bks[0];}

//djochange2
// add konqi compensation for cell/stretch layer discrepancy here
if (konqi == true) {
   aH += (2*bSIZE);
}
if(op6){aH-=bSIZE;}
//end djochange2

S+='<table cellpadding=0 cellspacing='+stbSize+' border=0 width='+bW+'  height='+aH+'><tr><td '+stretchLayer+' onmouseover="gridClearMenus()" onmousedown="aCM()">&nbsp;</td></tr></table>';
S+='</span>';
}

// event capturing layer
var GRw=bW;if(gridWIDTH>0){GRw=gridWIDTH;}
var GRh=bHe;if(gridHEIGHT>0){GRh=gridHEIGHT;}
var GRbc="";if(redGRID){GRbc="background-color:red";}
T+='<span id="grid" style="'+GRbc+'\;visibility:hidden\; position:absolute\; top:0\; left:0\; width:'+GRw+'\; height:'+GRh+'\; z-index:'+zORDER+'" onmousedown="aCM()" onmouseover="gridClearMenus()"></span>';


// open frame
if(!free){
T+='<span id="mainnav" style="'+spbk+' z-index:'+(zORDER+2)+'\; position:absolute\; top:'+aP[xp]+'px\; left:'+rL+'px\;"><table cellpadding=0 cellspacing='+bSIZE+' border=0><tr>';  
}

// link color behaviour
if(aHOVER==""){aHOVER=aLINK;}

var cSt=new Array;
var cTd=new Array;

var space=new Array;;

// alt text arrays
var aTxt=new Array;
var sTxt=new Array;
var cTxt=new Array;
var altNull='';

// write alt text to status bar 
function writeStatus(sTxt){window.status=sTxt; return true;}


// links
var cSt=new Array;
var linkHover='';
for (i=0;i<mI.length;i++){

aTxt[i]='';
if(mI[i][5]!="none"){
if(altDISPLAY=="title"){aTxt[i]=' title="'+mI[i][5]+'" ';}
if(altDISPLAY=="status"){aTxt[i]=' onmouseover="return writeStatus(mI['+i+'][5])" onmouseout="return writeStatus(altNull)" ';}
}

space[i]=' left:0px\;';
if(mI[i][3]=="left"){space[i]=' left:'+tINDENT+'px\;';}
if(mI[i][3]=="right"){space[i]=' left:-'+tINDENT+'px\;';}
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
if(free){
T+='<span style="'+cSt[i]+'\; '+spbk+' z-index:'+(zORDER+2)+'\; position:absolute\; top:'+aP[i]+'px\; left:'+aPL[i]+'px\;"><table cellpadding=0 cellspacing='+bSIZE+' border=0><tr>';
}
var op6a=0;if(op6){op6a=1;}
var op6b=0;if(op6){op6b=-1;}
T+='<td onmouseover="aCM()\; oM('+i+')" class=mTD height='+thisNH[i]+' '+bks[0]+' width="'+(mI[i][2])+'"><table cellpadding=0 cellspacing=0 border=0 width="'+(mI[i][2])+'" height="'+thisNH[i]+'" style="'+cTd[i]+'\;"><tr><td align="'+mI[i][3]+'" onclick="goToUrl(mI['+i+'][0],mI['+i+'][4])" '+aTxt[i]+'><a href="'+mI[i][0]+'" target="'+mI[i][4]+'" onmouseover="if(op6){linkClicked=true;}" onmouseout="if(op6){linkClicked=false;}" onclick="if((cellCLICK||(cellCLICK==mu))&&!op6){return false}" style="'+cSt[i]+'\; position:relative\; top:'+(vtOFFSET+op6b)+'\; '+space[i]+'"><font color="'+aLINK+'">'+mI[i][1]+'</font></a></td></tr></table></td>'; 
if(free){T+='</tr></table></span>';}
}
}

// close frame
if(!free){T+='</tr></table></span>';}


// rollovers
var layLeft=rL+bSIZE;
for (i=0;i<mI.length;i++){
if(free){layLeft=mI[i][7]+bSIZE+aL; xp=i;}
T+='<span id="roll'+i+'" style="'+cTd[i]+'\; position:absolute\; top:'+(aP[xp]+bSIZE)+'\; left:'+layLeft+'\; z-index:'+(zORDER+3)+'\; visibility:hidden\;" onmouseout="if(!keepLIT){miH(this)}"><table cellpadding=0 cellspacing=0 border=0 '+bks[2]+' onmouseover="oM('+i+')"><tr><td style="width:'+(mI[i][2])+'\; height:'+thisNH[i]+'\;" align="'+mI[i][3]+'" class=mTD onclick="goToUrl(mI['+i+'][0],mI['+i+'][4])" '+aTxt[i]+'><a href="'+mI[i][0]+'" target="'+mI[i][4]+'" onmouseover="if(op6){linkClicked=true;}" onmouseout="if(op6){linkClicked=false;}" onclick="if((cellCLICK||(cellCLICK==mu))&&!op6){return false}" style="'+cSt[i]+'\; position:relative\; top:'+(vtOFFSET+op6b)+'\; '+space[i]+'"><font color="'+aHOVER+'">'+mI[i][1]+'</font></a></td></tr></table></span>';
if(!free){layLeft+=(mI[i][2]+bSIZE);}
}



// assemble submenus

var M='';
var C='';

var SUBaL=0;

var chTop=new Array;
var Xrows=new Array;
var XCrows=new Array;

for (c=0;c<mI.length;c++){

Xrows[c]=0;
XCrows[c]=new Array;
sTxt[c]=new Array;
cTxt[c]=new Array;

// specify top position
if(free){xp=c;}

var mzSubAbsTop=sbSIZE;
chTop[c]=new Array;
chTop[c][0]=aP[xp]+thisAH[c]+vOFFSET+sP[c][3];

// text alignment and indentation
var space=' left:0px\;';
if(sP[c][2]=="left"){space=' left:'+stINDENT+'px\;';}
if(sP[c][2]=="right"){space=' left:-'+stINDENT+'px\;';}

// find next submenu position
if(!free){
if(c==0){
SUBaL=rL+bSIZE;
}
else {SUBaL+=mI[(c-1)][2]+bSIZE;}
} else {
SUBaL=aPL[c];
}

// specify edge alignment
var acL=SUBaL+hOFFSET+sP[c][4];
if(sP[c][1]=="right"){
acL=SUBaL-(sP[c][0]-mI[c][2])-hOFFSET-sP[c][4];
}

// grid block layer for vertical offset
if((vOFFSET+sP[c][3])>0){M+='<div id="gridblocker'+c+'" style="width:'+sP[c][0]+'px\; height:'+(vOFFSET+sP[c][3])+'\; visibility:hidden\; z-index:'+(zORDER+1)+'\;position:absolute\; top:'+(aP[xp]+thisAH[c])+'px\; left:'+acL+'px\;" onmouseover="if(rTimer!=0){clearTimeout(rTimer)\;rCount=0\;}"></div>';}

// grid block layer for horizontal offset
var gblw=acL-mI[c][2]-SUBaL;
var gbposL=(SUBaL+mI[c][2]+(2*bSIZE));
if(sP[c][1]=="right"){
gblw=mI[c][7]+bSIZE+aL-sP[c][0]-acL; 
gbposL=(mI[c][7]+bSIZE+aL-gblw);
}

for (i=0;i<sI[c].length;i++){
if(sI[c][i][0]!=''&&sI[c][i][1]!=''){
// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=sI[c][i][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { Xrows[c]+=(Trows-1); }
}}

// grid blocker for offset
var shb=0;if(shy){shb=(2*shSIZE);}
if(gblw>0){M+='<div id="gridLblocker'+c+'" style="width:'+gblw+'px\; height:'+(((snH+sbSIZE)*sI[c].length)+((snH)*Xrows[c])+sbSIZE+shb+shSIZE)+'px\; visibility:hidden\; z-index:'+(zORDER+1)+'\;position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3])+'px\; left:'+gbposL+'px\;" onmouseover="if(rTimer!=0){clearTimeout(rTimer)\;rCount=0\;}"></div>';}


// container cells
M+='<span id="subnav'+c+'" style="visibility:hidden\; z-index:'+(zORDER+4)+'\; position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3])+'px\; left:'+acL+'px\;"><table cellpadding=0 cellspacing=0 width="'+sP[c][0]+'" border=0 '+bks[4]+'>';
for (i=0;i<sI[c].length;i++){
if(sI[c][i][0]!=''&&sI[c][i][1]!=''){
var subParent='subnav'+c;

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=sI[c][i][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

//djochange2 (removed)
var kAdjust=0;

//311kde change - added physical spacer between cells to compensate for lack of css margin support
if(konqi){M+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';}

M+='<tr><td class=SUBmTD><table cellpadding=0 cellspacing=0 border=0 '+bks[3]+' style="position:relative\; margin-top:'+sbSIZE+'\; margin-left:'+sbSIZE+'\; margin-right:'+sbSIZE+'\; height:'+(thisH+kAdjust)+'\;" width="'+(sP[c][0]-(sbSIZE*2))+'"><tr><td class=SUBmTD align="'+sP[c][2]+'"><table cellpadding=0 cellspacing=0 border=0 width="'+(sP[c][0]-(sbSIZE*2))+'" height='+(thisH+kAdjust)+'><tr><td class=SUBmTD align="'+sP[c][2]+'">&nbsp;</td></tr></table></td></tr></table></td></tr>';
}
}
M+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';
M+='</table></span>';

// link color behaviour
if(saHOVER==""){saHOVER=saLINK;}

// links
M+='<span id="sublinks'+c+'" style="visibility:hidden\; z-index:'+(zORDER+5)+'\; position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3])+'px\; left:'+acL+'px\;"><table cellpadding=0 cellspacing=0 width="'+sP[c][0]+'" border=0>'; 

var scSt=new Array;
var scTd=new Array;
for (i=0;i<sI[c].length;i++){

cTxt[c][i]=new Array;
sTxt[c][i]='';
if(sI[c][i][3]!="none"){
if(altDISPLAY=="title"){sTxt[c][i]=' title="'+sI[c][i][3]+'" ';}
if(altDISPLAY=="status"){sTxt[c][i]=' onmouseover="return writeStatus(sI['+c+']['+i+'][3])" onmouseout="return writeStatus(altNull)" ';}
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
var subParent='subnav'+c;

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=sI[c][i][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

chTop[c][i+1]=chTop[c][i]+thisH+sbSIZE;


//djochange2 (removed)
//added kAdjust for item height
var kAdjust=0;

//311kde change - added physical spacer between cells to compensate for lack of css margin support
if(konqi){M+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';}

M+='<tr><td><table border=0 cellpadding=0 cellspacing=0 style="position:relative\; top:'+mzSubAbsTop+'\; margin-top:0\; margin-left:'+sbSIZE+'\; margin-right:'+sbSIZE+'\; " onmouseover="if(rTimer!=0){clearTimeout(rTimer)\;rCount=0\;}cCM('+c+','+i+')\; oCM('+c+','+i+')\;"><tr><td class=SUBmTD align="'+sP[c][2]+'" width="'+(sP[c][0]-(sbSIZE*2))+'" height='+(thisH+kAdjust)+' onclick="goToUrl(sI['+c+']['+i+'][0],sI['+c+']['+i+'][2])" '+sTxt[c][i]+'><a href="'+sI[c][i][0]+'" id="sublink'+c+'-'+i+'" target="'+sI[c][i][2]+'" onmouseover="if(op6){linkClicked=true;}" onmouseout="if(op6){linkClicked=false;}" onclick="if((cellCLICK||(cellCLICK==mu))&&!op6){return false}" style="'+scSt[i]+'\; position:relative\; top:'+(svtOFFSET)+'\; '+space+'">'+ sI[c][i][1]+'</a></td></tr></table></td></tr>';
// correct for positioning discrepancy 

// added Konqi to this line 
if(mz7||ns61||konqi){mzSubAbsTop += sbSIZE;}

}
M+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';
M+='</table></span>';


// rollovers
for (i=0;i<sI[c].length;i++){

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=sI[c][i][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

M+='<span id="subroll'+c+'-'+i+'" style="'+scTd[i]+'\; z-index:'+(zORDER+5)+'\; position:absolute; top:'+(chTop[c][i]+sbSIZE)+'\; left:'+(acL+sbSIZE)+'\; visibility:hidden\;" onmouseover="if(rTimer!=0){clearTimeout(rTimer)\;rCount=0\;}" onmouseout="if(!keepSubLIT){miH(this)}">';
M+='<table cellpadding=0 cellspacing=0 border=0 '+bks[5]+' onmouseover="cCM('+c+','+i+')\; oCM('+c+','+i+')\;"><tr><td class=SUBmTD align="'+sP[c][2]+'" width="'+(sP[c][0]-(sbSIZE*2))+'" height='+thisH+' onclick="goToUrl(sI['+c+']['+i+'][0],sI['+c+']['+i+'][2])" '+sTxt[c][i]+'><a href="'+sI[c][i][0]+'" id="srolllink'+c+'-'+i+'" target="'+sI[c][i][2]+'" onmouseover="if(op6){linkClicked=true;}" onmouseout="if(op6){linkClicked=false;}" onclick="if((cellCLICK||(cellCLICK==mu))&&!op6){return false}" style="'+scSt[i]+'\; position:relative\; top:'+(svtOFFSET)+'\; '+space+'"><font color="'+saHOVER+'">'+ sI[c][i][1]+'</font></a></td></tr></table>';
M+='</span>';
}


// drop shadow 
var shb=0;if(shy){shb=(2*shSIZE);}
if(shCOLOR!=""){M+='<span id="shadow'+c+'" style="visibility:hidden\; -moz-opacity:'+mzOpac+'\; background-image:url('+bh + shCOLOR+')\; background-color:'+shCOLOR+'\; z-index:'+(zORDER+3)+'\; position:absolute\; top:'+(aP[xp]+thisAH[c]+vOFFSET+sP[c][3]+shSIZE-shb)+'px\; left:'+(acL+shSIZE-shb)+'px\; width:'+(sP[c][0]+shb)+'\; height:' + (((snH+sbSIZE)*sI[c].length)+((snH)*Xrows[c])+sbSIZE+shb) + 'px\;"></span>';}



//alert("assemble child menus");

for (sq=0;sq<sI[c].length;sq++){

if(cP[c][sq].length>0&&cI[c][sq].length>0){

XCrows[c][sq]=0;

// specify edge alignment
var chL;
if(cP[c][sq][1]=="right"){
chL=acL-cP[c][sq][0]-chhOFFSET-cP[c][sq][4];
cgL=chL+cP[c][sq][0];
} else {
chL=acL + sP[c][0]+chhOFFSET+cP[c][sq][4];
cgL=(chL-(chhOFFSET+cP[c][sq][4]));
}

for (cj=0;cj<cI[c][sq].length;cj++){
// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=cI[c][sq][cj][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { XCrows[c][sq]+=(Trows-1); }
}

// grid block layer for offset
if((chhOFFSET+cP[c][sq][4])>0){
C+='<span id="childgrid'+c+'-'+sq+'" style="visibility:hidden\; z-index:'+(zORDER+1)+'\; position:absolute\; top:'+((chTop[c][sq]+chvOFFSET)+cP[c][sq][3])+'px\; left:'+cgL+'px\; width:'+(chhOFFSET+cP[c][sq][4])+'px\; height:'+(((snH+sbSIZE)*cI[c][sq].length)+((snH)*XCrows[c][sq])+sbSIZE+shb+shSIZE)+'px\;" onmouseover="if(rTimer!=0){clearTimeout(rTimer)\;rCount=0\;}"></span>';
}

// container cells
C+='<span id="childnav'+c+'-'+sq+'" style="visibility:hidden\; z-index:'+(zORDER+6)+'\; position:absolute\; top:'+ (chTop[c][sq]+chvOFFSET+cP[c][sq][3])+'px\; left:'+chL+'px\;"><table cellpadding=0 cellspacing=0 width="'+cP[c][sq][0]+'" border=0 '+bks[4]+'>'; 
for (cj=0;cj<cI[c][sq].length;cj++){

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=cI[c][sq][cj][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }




//djochange2 (removed)
//added kAdjust for item height
var kAdjust=0;

//311kde change - added physical spacer between cells to compensate for lack of css margin support
if(konqi){C+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';}

C+='<tr><td class=SUBmTD><table cellpadding=0 cellspacing=0 '+bks[3]+' border=0 style="position:relative\; margin-top:'+sbSIZE+'\; margin-left:'+sbSIZE+'\; margin-right:'+sbSIZE+'\; height:'+(thisH+kAdjust)+'\;" width="'+(cP[c][sq][0]-(sbSIZE*2))+'"><tr><td class=SUBmTD align="'+cP[c][sq][2]+'"><table cellpadding=0 cellspacing=0 border=0 width="'+(cP[c][sq][0]-(sbSIZE*2))+'" height='+(thisH+kAdjust)+'><tr><td class=SUBmTD align="'+cP[c][sq][2]+'">&nbsp;</td></tr></table></td></tr></table></td></tr>';
}
C+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';
C+='</table></span>';

// text alignment and indentation
var cspace=' left:0px\;';
if(cP[c][sq][2]=="left"){cspace=' left:'+stINDENT+'px\;';}
if(cP[c][sq][2]=="right"){cspace=' left:-'+stINDENT+'px\;';}

// links
C+='<span id="childlinks'+c+'-'+sq+'" style="visibility:hidden\; z-index:'+(zORDER+7)+'\; position:absolute\; top:'+(chTop[c][sq]+chvOFFSET+cP[c][sq][3])+'px\; left:'+chL+'px\;"><table cellpadding=0 cellspacing=0 width="'+cP[c][sq][0]+'" border=0>'; 

// added Konqi to this line 
if(mz7||ns61||konqi){mzSubAbsTop=sbSIZE;}

for (cj=0;cj<cI[c][sq].length;cj++){

cTxt[c][sq][cj]='';
if(cI[c][sq][cj][3]!="none"){
if(altDISPLAY=="title"){cTxt[c][sq][cj]=' title="'+cI[c][sq][cj][3]+'" ';}
if(altDISPLAY=="status"){cTxt[c][sq][cj]=' onmouseover="return writeStatus(cI['+c+']['+sq+']['+cj+'][3])" onmouseout="return writeStatus(altNull)" ';}
}

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=cI[c][sq][cj][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }


//djochange2 (removed)
//added kAdjust for item height
var kAdjust=0;

//311kde change - added physical spacer between cells to compensate for lack of css margin support
if(konqi){C+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';}

var ccAl='cursor:'+aCURSOR;
var ccTd='cursor:default';if(cellCLICK==true){ccTd='cursor:'+aCURSOR;}
if(!cI[c][sq][cj][4]){ccAl='cursor:default';ccTd='cursor:default';}
if(cI[c][sq][cj][0]=="#"){cI[c][sq][cj][0]="javascript:doNothing()";}
C+='<tr><td class=SUBmTD><table cellpadding=0 cellspacing=0 border=0 style="position:relative\; top:'+mzSubAbsTop+'\; margin-top:0\; margin-left:'+sbSIZE+'\; margin-right:'+sbSIZE+'\; " onmouseover="if(rTimer!=0){clearTimeout(rTimer)\;rCount=0\;}if(cI['+c+']['+sq+']['+cj+'][4]){oR[11]['+c+']['+sq+']['+cj+'].style.visibility=\'visible\'\;}"  onmouseout="if(oR[11]['+c+']['+sq+']['+cj+']){oR[11]['+c+']['+sq+']['+cj+'].style.visibility=\'hidden\'\;}"><tr><td class=SUBmTD align="'+cP[c][sq][2]+'" width="'+(cP[c][sq][0]-(sbSIZE*2))+'" height='+(thisH+kAdjust)+' onclick="goToUrl(cI['+c+']['+sq+']['+cj+'][0],cI['+c+']['+sq+']['+cj+'][2])" '+cTxt[c][sq][cj]+'><a href="'+cI[c][sq][cj][0]+'" id="childlink'+c+'-'+sq+'-'+cj+'" target="'+cI[c][sq][cj][2]+'" onmouseover="if(op6){linkClicked=true;}" onmouseout="if(op6){linkClicked=false;}" onclick="if((cellCLICK||(cellCLICK==mu))&&!op6){return false}" style="' +ccAl+'\; position:relative\; top:'+(svtOFFSET)+'\; '+cspace+'">'+ cI[c][sq][cj][1]+'</a></td></tr></table></td></tr>';
// correct for positioning discrepancy

// added Konqi to this line 
if(mz7||ns61||konqi){mzSubAbsTop += sbSIZE;}

}
C+='<tr><td><div style="width:2px\; height:'+sbSIZE+'px\;"></div></td></tr>';
C+='</table></span>';
}

var thisCHT = chTop[c][sq]+chvOFFSET+cP[c][sq][3]+sbSIZE;
// rollovers
for (cj=0;cj<cI[c][sq].length;cj++){

// find wrapping links routine by brendan armstrong
var thisH=snH;
var thisT=cI[c][sq][cj][1];
var splitT=thisT.split('<br>');
var Trows=splitT.length;
if (Trows>1) { thisH=(snH*Trows); }

var ccAl='cursor:'+aCURSOR;
var ccTd='cursor:default';if(cellCLICK==true){ccTd='cursor:'+aCURSOR;}
if(!cI[c][sq][cj][4]){ccAl='cursor:default';ccTd='cursor:default';}
C+='<span id="childroll'+c+'-'+sq+'-'+cj+'" style="'+ccTd+'\; visibility:hidden\; z-index:'+(zORDER+8)+'\; position:absolute\; top:'+thisCHT+'px\; left:'+(chL+sbSIZE)+'px\;" onmouseover="if(rTimer!=0){clearTimeout(rTimer)\;rCount=0\;}this.style.visibility=\'visible\'" onmouseout="miH(this)"><table cellpadding=0 cellspacing=0 border=0 '+bks[5]+' onmouseout="this.parentNode.style.visibility=\'hidden\'\;"><tr><td class=SUBmTD align="'+cP[c][sq][2]+'" width="'+(cP[c][sq][0]-(sbSIZE*2))+'" height='+thisH+' onclick="goToUrl(cI['+c+']['+sq+']['+cj+'][0],cI['+c+']['+sq+']['+cj+'][2])" '+cTxt[c][sq][cj]+'><a id="crolllink'+c+'-'+sq+'-'+cj+'" href="'+cI[c][sq][cj][0]+'" target="'+cI[c][sq][cj][2]+'" onmouseover="if(op6){linkClicked=true;}" onmouseout="if(op6){linkClicked=false;}" onclick="if((cellCLICK||(cellCLICK==mu))&&!op6){return false}" style="'+ccAl+'\; position:relative\; top:'+(svtOFFSET)+'\; '+cspace+'"><font color="'+saHOVER+'">'+ cI[c][sq][cj][1]+'</font></a></td></tr></table></span>';
thisCHT+=(thisH+sbSIZE);
}


// drop shadow 
if(shCOLOR!=""){C+='<span id="childshadow'+c+'-'+sq+'" style="visibility:hidden\; -moz-opacity:'+mzOpac+'\; background-image:url('+bh + shCOLOR+')\; background-color:'+shCOLOR+'\; z-index:'+(zORDER+5)+'\; position:absolute\; top:'+((chTop[c][sq]+chvOFFSET)+shSIZE+cP[c][sq][3]-shb)+'px\; left:'+(chL+shSIZE-shb)+'px\; width:'+(cP[c][sq][0]+shb)+'\; height:' + (((snH+sbSIZE)*cI[c][sq].length)+((snH)*XCrows[c][sq])+sbSIZE+shb) + 'px\;"></span>';}
}
}



// resize / reload trap

function nsinit(){if(allowRESIZE==true){setTimeout("window.onresize=redo", 1000);}}
function redo(){window.location.reload();}
if(mz7){window.onload=nsinit;}
else {if(allowRESIZE==true){window.onresize=new Function("window.location.reload()");}}




// draw stretch layer 
if(!free){d.write(S);}


// draw main nav
d.write(T);


// draw submenus
d.write(M);


// draw child menus
d.write(C);





window.onload=storeObjects;



document.onclick=aCM;


