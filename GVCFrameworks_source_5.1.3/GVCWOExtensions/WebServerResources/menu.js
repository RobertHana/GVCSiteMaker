//UDMv3.2.2
	
	
var siSTR='';
if (ie4||(mac&&ie5)) { siSTR+='<script language=javascript src="' + baseHREF + 'menu_ie4.js"></script>'; }
if (win&&ie5) { siSTR+='<script language=javascript src="' + baseHREF + 'menu_ie5.js"></script>'; }
if (ns4) { siSTR+='<script language=javascript src="' + baseHREF + 'menu_ns4.js"></script>'; }
//djochange
if (ns6||mz7||konqi||op6) { siSTR+='<script language=javascript src="' + baseHREF + 'menu_ns6.js"></script>'; }
//end djochange
if (op5) { siSTR+='<script language=javascript src="' + baseHREF + 'menu_op5.js"></script>'; }


document.write(siSTR);	
	


