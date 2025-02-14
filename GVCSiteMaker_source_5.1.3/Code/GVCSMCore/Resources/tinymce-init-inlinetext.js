tinyMCE.init({
          mode: "textareas",
          width: "100%",
          editor_selector : "advancedEditor",
          theme : "advanced",
          relative_urls : true,      // Needed for anchors
          convert_urls : false,      // Needed for images
          remove_script_host : true, // Needed for images
          theme_advanced_source_editor_wrap : true,
          apply_source_formatting : true,
          plugins : "media,contextmenu,directionality,table,save,advhr,advimage,advlink,preview,style,safari,noneditable,paste",
          theme_advanced_toolbar_location : "external",
          theme_advanced_toolbar_align : "center",
          theme_advanced_resizing : true,
          theme_advanced_buttons1: "bold,italic,underline,forecolor,backcolor,styleprops,separator,justifyleft,justifycenter,justifyright,justifyfull,separator,styleselect,formatselect,fontselect,fontsizeselect",
          theme_advanced_buttons2: "image, media, table,link,unlink,anchor,advhr,charmap,pasteword,separator,bullist,numlist,indent,outdent,separator,sub,sup,separator,ltr,rtl,separator,removeformat,code,undo,redo,fullscreen",
          theme_advanced_buttons3: "",
          /*
          extended_valid_elements : "a[name|href|target|title|onclick],img[class|src|border=0|alt|title|hspace|vspace|width|height|align|onmouseover|onmouseout|name],hr[class|width|size|noshade],font[face|size|color|style],span[class|align|style],td[width|height|align|valign|bgcolor],th[width|height|align|valign|bgcolor],style,WebObject[name],script",
          */
          extended_valid_elements : "*[*]",
          auto_cleanup_word : true,
          force_p_newlines : false,
          force_br_newlines : true,
          setupcontent_callback : "myCustomSetupContent"
        });
