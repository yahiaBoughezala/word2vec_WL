#coding=utf-8
'''
send mail
 '''
import smtplib,email
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.header import Header
from log import MyLogger
from conf.email import MailServer

class MonitorEmail:

    def __init__(self, to_addr=[], cc_addr=[]):
        self.server=smtplib.SMTP(MailServer.SMTP_SERVER, MailServer.SMTP_PORT)
        self.server.ehlo()
        self.server.login(MailServer.SMTP_USER, MailServer.SMTP_PASSWORD)
        self.to_addr = to_addr
        self.cc_addr = cc_addr
        self.logger = MyLogger.getLogger()

    def add_suff(self, addr):
        return str(addr)+'@9icaishi.net'

    def send_message(self, subj='', content='', attach=None):
        COMMASPACE = ', '
        msg = MIMEMultipart()
        msg['From'] = MailServer.SMTP_SEND_FROM
        msg['CC'] = COMMASPACE.join(map(self.add_suff, self.cc_addr))
        msg['To'] =COMMASPACE.join(map(self.add_suff, self.to_addr))
        msg['Subject'] = Header(subj, 'utf-8')
        msg['Date']= email.Utils.formatdate()
        # msg.set_payload(content)
        if not attach:
            msg.attach(MIMEText(content, _charset='utf-8'))
        else:
            msg.attach(MIMEText(content, _charset='utf-8'))
            msg.attach(attach)
        try:
            self.server.sendmail(MailServer.SMTP_SEND_FROM, map(self.add_suff, self.to_addr), msg.as_string()) #may also raise exc
        except Exception, ex:
            import traceback
            self.logger.error("Error: Send mail failed")
            self.logger.error(traceback.print_exc())
            self.logger.error(ex)
        else:
            self.logger.info("Send mail successfully")



if __name__=="__main__":
    pass


