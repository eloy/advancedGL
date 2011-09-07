require 'builder'
require 'pp'


def parse_line(text, acct_str, line)
  line.text text
  line.bold false
  line.identation 0
  line.id "line"
  if acct_str
    acct_str.chomp!
    # clean a bit from csv
    acct_str.gsub!(".",",")

    # split
    accounts = acct_str.split(',')
    accounts.each do |acct|
      line.account acct
    end
  end

end

def get_pages
  pages = []
  filename ="/home/harlock/Desktop/work/opensixen/balances/importacion/final/out.csv"
  file = File.new(filename)
  current = []
  file.readlines.each do |line|
    content = line.split(';')
    if content.length == 2
      acct = content[0]
      text = content[1].chomp
      if text == "--BREAK--"
        pages.push current
        current = []
      else
        current.push Hash[:text=>text, :acct=>acct]
      end

    end

  end
  pages.push current
  return pages
end


b = Builder::XmlMarkup.new(:indent => 1)
xml = b.finreport do |report|

  get_pages.each do |page|
    report.page do |p|
      page.each do |line|
        p.line do |report_line|
          parse_line(line[:text], line[:acct],report_line)
        end
      end
    end
  end
end

puts xml
