camFile = open('camFindOut.txt', 'r')
fileContent = camFile.read()
splitContent = fileContent.split()

catWords = ['cat', 'kitten', 'feline', 'lion', 'tiger', 'lynx', 'cougar', 'leopard']

catContent = "No cat"
for catWord in catWords:
	#print catWord
	if catWord in splitContent:
		catContent = "Yes cat"
	
#splitContent = fileContent.split()	
delim = len(splitContent)/3
remainder = len(splitContent)%3

outputContent = []
if remainder == 0:
	outputContent.append(splitContent[:delim])
	outputContent.append(splitContent[delim:(2*delim)])
	outputContent.append(splitContent[(2*delim):])
elif remainder == 1:
	outputContent.append(splitContent[:delim])
	outputContent.append(splitContent[delim:(2*delim)+1])
	outputContent.append(splitContent[(2*delim)+1:])
elif remainder == 2:
	outputContent.append(splitContent[:delim+1])
	outputContent.append(splitContent[delim+1:(2*delim)+1])
	outputContent.append(splitContent[(2*delim)+1:])

xmlOut = """<?xml version = "1.0" encoding = "UTF-8" ?>
    <HeaderFooterSettings version = "8.0">
        <Font type="TrueType" size="12.0" name="Arial"/>
        <Color r="1.0" b="0.0" g="0.0"/>
        <Margin right="36.0" left="36.0" bottom="36.0" top="36.0"/>
        <Appearance fixedprint="0" shrink="0"/>
        <PageRange odd="1" even="1" end="-1" start="-1"/>
        <Page offset = "0">
            <PageIndex format="1"/>
        </Page>
        <Date><Month format="1"/>/<Day format="1"/><Year format="0"/></Date>
        <Header>
        	<Left>{}</Left>
            <Center>{}</Center>
            <Right>{}</Right>
    	</Header>
        <Footer>
            <Center>{}</Center>
        </Footer>
    </HeaderFooterSettings>
""".format(str.join(' ', outputContent[0]), str.join(' ',outputContent[1]), str.join(' ',outputContent[2]), catContent)

#print xmlOut
file = open('outputXML.xml', 'w')
file.write(xmlOut)
file.close()
