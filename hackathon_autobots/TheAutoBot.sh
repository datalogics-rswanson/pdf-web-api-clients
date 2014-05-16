#!/bin/bash

PDFINPUT=$(echo $1 | sed -e "s/.pdf//")

# Strip .pdf extension if found
#echo $1 | sed 's/.pdf//

OPTIONS='{"printPreview": true, "outputFormat": "jpg"}'

#echo $OPTIONS

python pdfprocess.py RenderPages "$PDFINPUT.pdf" options="$OPTIONS"
python camFindClient.py "$PDFINPUT.jpg"


cat camFindOut.txt 
echo ''

python fillXML.py
python fillFDF.py

python pdfprocess.py FillForm TheAutoBotForm.pdf outputFDF.fdf inputName=FilledForm

java -jar DecorateDoc.jar "$PDFINPUT.pdf" outputXML.xml DecoratedForm.pdf

python pdfprocess.py ExportFormData FilledForm.pdf
python pdfprocess.py FlattenForm FilledForm.pdf

open 'FilledForm.pdf'
open 'DecoratedForm.pdf'
