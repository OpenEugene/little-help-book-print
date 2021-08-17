java -cp ..\target\LittleHelpBook.jar org.eugenetech.littlehelpbook.BuildBook --settings little-help-book.properties

"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --headless --disable-gpu --print-to-pdf-no-header --print-to-pdf="c:\workspace\eugenetech\little-help-book-print\data\english.pdf" "c:\workspace\eugenetech\little-help-book-print\data\english.html"
"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" --headless --disable-gpu --print-to-pdf-no-header --print-to-pdf="c:\workspace\eugenetech\little-help-book-print\data\spanish.pdf" "c:\workspace\eugenetech\little-help-book-print\data\spanish.html"

