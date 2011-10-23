require 'builder'
require 'pp'
require 'rexml/document'


# extract event information
doc = REXML::Document.new(File.new("/home/harlock/git/advancedGL/es.indeos.osx.finreports/xml/balance.xml"))
#pp doc
doc.elements.each('finreport/page') do |page|
  page.elements.each do |l|
    title = l.elements["text"].text
    accounts = []
    l.elements.each('account') do |a|
      accounts.push a.text
    end

    puts "#{title}\t#{accounts.join(',')}"
  end

  puts "###########  SALTO \tDE PAGINA ##############"
end
