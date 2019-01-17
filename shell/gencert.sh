# Generate SSL Certificate Files

CACOUNTRY=CN
CAPROVINCE=Beijing
CACITY=Beijing
CAORGANIZATION=MOE
CAUNIT=MOE
CACOMMONNAME=MOE
CAEMAIL=ymwang@moe.edu.cn

SERVERCOUNTRY=CN
SERVERPROVINCE=Hubei
SERVERCITY=Wuhan
SERVERORGANIZATION=HUST
SERVERUNIT=HUST
SERVERCOMMONNAME=HUST
SERVEREMAIL=ymwang@hust.edu.cn

DUEDAYS=3650

CERTDIR=./ssl.crt

if [ "$1" != "" ]; then
  SERVERCOMMONNAME=$1
fi

if [ "$2" != "" ]; then
  CERTDIR=$2
fi

mkdir -p $CERTDIR

echo ""
echo -n ">>>> Generate CA private key \"ssl-ca.key\"..."
echo ""
# encrypt the key file, need password
#openssl genrsa -aes256 -out $CERTDIR/ssl-ca.key 2048
# Don't encrypt the key file, not need password
openssl genrsa -out $CERTDIR/ssl-ca.key 2048
echo "OK."
echo ""
echo ">> Show details of the generated CA private key \"ssl-ca.key\"..."
echo ""
openssl rsa -noout -text -in $CERTDIR/ssl-ca.key

echo ""
echo -n ">>>> Generate CA self-signed certificate \"ssl-ca.crt\"..."
echo ""
openssl req -new -x509 -days $DUEDAYS -subj "/C=$CACOUNTRY/ST=$CAPROVINCE/L=$CACITY/O=$CAORGANIZATION/OU=$CAUNIT/CN=$CACOMMONNAME/emailAddress=$CAEMAIL" -key $CERTDIR/ssl-ca.key -out $CERTDIR/ssl-ca.crt
echo "OK."
echo ""
echo ">> Show details of the generated CA certificate \"ssl-ca.crt\"..."
echo ""
openssl x509 -noout -text -in $CERTDIR/ssl-ca.crt

echo ""
echo -n ">>>> Generate server private key \"ssl-server.key\"..."
echo ""
# encrypt the key file, need password
#openssl genrsa -aes256 -out $CERTDIR/ssl-server.key 2048
# Don't encrypt the key file, not need password
openssl genrsa -out $CERTDIR/ssl-server.key 2048
echo "OK."
echo ""
echo ">> Show details of the generated server private key \"ssl-server.key\"..."
echo ""
openssl rsa -noout -text -in $CERTDIR/ssl-server.key

echo ""
echo -n ">>>> Generate server unsigned certificate \"ssl-server.csr\"..."
echo ""
openssl req -new -subj "/C=$SERVERCOUNTRY/ST=$SERVERPROVINCE/L=$SERVERCITY/O=$SERVERORGANIZATION/OU=$SERVERUNIT/CN=$SERVERCOMMONNAME/emailAddress=$SERVEREMAIL" -key $CERTDIR/ssl-server.key -out $CERTDIR/ssl-server.csr
echo "OK."
echo ""
echo ">> Show details of the generated server unsigned certificate \"ssl-server.csr\"..."
echo ""
openssl req -noout -text -in $CERTDIR/ssl-server.csr

echo ""
echo -n ">>>> Generate server signed certificate \"ssl-server.crt\"..."
echo ""
openssl x509 -req -days $DUEDAYS -in $CERTDIR/ssl-server.csr -CA $CERTDIR/ssl-ca.crt -CAkey $CERTDIR/ssl-ca.key -set_serial $RANDOM -out $CERTDIR/ssl-server.crt
#openssl x509 -req -days $DUEDAYS -in $CERTDIR/ssl-server.csr -signkey $CERTDIR/ssl-server.key -out $CERTDIR/ssl-server.crt
echo "OK."
echo ""
echo ">> Show details of the generated server signed certificate \"ssl-server.crt\"..."
echo ""
openssl x509 -noout -text -in $CERTDIR/ssl-server.crt

chmod u-w,go-rwx -R $CERTDIR
