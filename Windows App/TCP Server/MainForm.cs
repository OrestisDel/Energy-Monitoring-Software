using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.Text.RegularExpressions;

namespace login
{

    public partial class frmmain : System.Windows.Forms.Form
    {


        private static Socket serverSocket;
        private static List<Socket> ClientSocket = new List<Socket>();
        private static byte[] buffer = new byte[1024];

        string filepath = @"C:\Users\Orestis\Desktop\Test\Exprt_txt.txt";
        string filename = "Exprt_txt.txt";
		
		

        public static System.Windows.Forms.DateTimePicker DateTimePicker1 { get; set; }
        public static string strDate;
        public static string strDate2;
        public static System.Windows.Forms.DateTimePicker DateTimePicker2 { get; set; }

        public bool check1;

        public frmmain()
        {
            InitializeComponent();

            IPAddress localIP = IPAddress.Parse(GetLocalIPAddress());
			//initialize dateTimePicker1, dateTimePicker2 and btnSend disabled controls
            dateTimePicker1.Enabled = false;
            dateTimePicker2.Enabled = false;
            btnSend.Enabled = false;
			//set boolean check1 true
            check1 = true;
        }

        private void Btm_LogOut_Click(object sender, EventArgs e)
        {
			//if checkBox1 is checked the process immediately stops else it returns to the Login Form
            if (checkBox1.Checked)
            {
                System.Diagnostics.Process.GetCurrentProcess().Kill();
                Application.Exit();
            }
            else
            {
                this.Hide();
                frmlogin fl = new frmlogin();
                fl.Show();
            }

        }
        private void frmmain_FormClosing(object sender, FormClosingEventArgs e)
        {
            Application.Exit();
        }
		
		//if button1 is clicked datetimepicker1 and dateTimePicker2 values are converted to strings
		// messagebox containing their values and the check1 value are generated
        private void Button1_Click(object sender, EventArgs e)
        {
            DateTime dateToday = dateTimePicker1.Value;

            string strDate = dateToday.ToString();
            DateTime dateToday2 = dateTimePicker2.Value;

            string strDate2 = dateToday2.ToString();
            MessageBox.Show(check1.ToString());
            MessageBox.Show(strDate);
            MessageBox.Show(strDate2);
        }

		//if button2 is clicked dateTimePicker1 and dateTimePicker2 values are converted to strings
		//the strings and check1 values are transferred to the frmtabledata Form instance
        private void Button2_Click(object sender, EventArgs e)
        {
            DateTime dateToday = dateTimePicker1.Value;

            string strDate = dateToday.ToString();
            DateTime dateToday2 = dateTimePicker2.Value;

            string strDate2 = dateToday2.ToString();

            frmtabledata td = new frmtabledata();
            td.strD = strDate;
            td.strD2 = strDate2;

            td.check = check1;
            td.Show();
        }

		//The function returns a string with the localhost IP Address fetched from the DNS server
        static string GetLocalIPAddress()
        {
            var host = Dns.GetHostEntry(Dns.GetHostName());
            foreach (var ip2 in host.AddressList)
            {
                if (ip2.AddressFamily == AddressFamily.InterNetwork)
                {
                    return ip2.ToString();
                }
            }
            throw new Exception("No network adapters with an IPv4 address in the system!");
        }


		//when button3 is clicked txtHost textbox displays the local IP address 
        private void button3_Click(object sender, EventArgs e)
        {
            txtHost.Text = GetLocalIPAddress();
        }

	
        public void btnStart_Click(object sender, EventArgs e)
        {

            //Start server host
            txtStatus.Text += "Server starting...\r\n";

            int pt = int.Parse(txtPort.Text);
            try
            {
				//if a file exist in this directory, then delete it
                if (!File.Exists(@"C:\Users\Orestis\Desktop\Test\Exprt_txt.txt"))
                {
                    File.Create(@"C:\Users\Orestis\Desktop\Test\Exprt_txt.txt");
                }
				//initializes a new instance of the socket class using the specified address family, socket type and protocol
                serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                //Associates a socket with a new instances of the local end point and a specified port number
				serverSocket.Bind(new IPEndPoint(IPAddress.Any, pt));
				//Listens for connection requests on that specified end point
                serverSocket.Listen(0);
                //begins an asynchronous operation to accept an incoming connection attempt
                serverSocket.BeginAccept(new AsyncCallback(AcceptCallback), null);

                txtStatus.Text += "Server started...\r\n";
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void AcceptCallback(IAsyncResult ar)
        {

            try
            {
				//Accepts an incoming connection attempt and add a client socket to the end of the socket list
                Socket socket = serverSocket.EndAccept(ar);
                ClientSocket.Add(socket);
				//begins to asynchronously receive data from the socket
                socket.BeginReceive(buffer, 0, buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);
                //creates a loop by begin accepting connection attempts on the server socket
                serverSocket.BeginAccept(new AsyncCallback(AcceptCallback), null);

            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void ReceiveCallback(IAsyncResult ar)
        {

            try
            {
				//gets the socket
                Socket socket = (Socket)ar.AsyncState;
				//ends a pending asynchronous read
                int received = socket.EndReceive(ar);
				//creates a data buffer of length equal to the received bytes
                byte[] dataBuf = new byte[received];
				//copies the received buffer to the dataBuf of equal length
                Array.Copy(buffer, dataBuf, received);
				//converts  ASCII character set to string and appends to text box
                string text = Encoding.ASCII.GetString(dataBuf);
                AppendToTextBox(text);
                string response = string.Empty;
				//if text is not null then sends a response string
                if (text == null)
                {
                    response = "Retry";
                }
                else
                {
                    response = "TCP Connection established \r\n\tServer IP is: " + GetLocalIPAddress() + "\r\n";
                }


				//Encodes ASCII character set to bytes
                byte[] data = Encoding.ASCII.GetBytes(response);
				//sends data asynchronously to a connected socket
                socket.BeginSend(data, 0, data.Length, SocketFlags.None, new AsyncCallback(SendCallback), socket);
				//resizes the buffer to the received buffer size
                Array.Resize(ref buffer, socket.ReceiveBufferSize);
				//creates a loop to asynchronously receive data from a connected socket 
                socket.BeginReceive(buffer, 0, buffer.Length, SocketFlags.None, new AsyncCallback(ReceiveCallback), socket);

            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }


        }

        private void SendCallback(IAsyncResult ar)
        {
            try
            {
				//gets client socket
                Socket socket = (Socket)ar.AsyncState;
				//ends a pending asynchronous send
                socket.EndSend(ar);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void AppendToTextBox(string text)
        {
			//call a method invoker to invoke the text displayed on the txtStatus text box 
            MethodInvoker invoker = new MethodInvoker(delegate ()
            {
                txtStatus.Text += "\n" + "Client: " + text + "\r\n" + "\r\n";
            });
            this.Invoke(invoker);


        }
		//defines a regular expression as white space
		private readonly Regex regex = new Regex(@"/s+");
		
		// replaes a string input white spaces with empty space
        public string RemoveWhiteSpaces(string str)
        {
            return regex.Replace(str, String.Empty);
        }
		
		//Gets clientIP from the confirmation message received from client
        public string ClientIP()
        {
            string code = "";
            string obj = txtStatus.Text;
			//defines a string to be searched 
            string toBeSearched = "IP: ";
			//defines the index of that string
            int ix = obj.IndexOf(toBeSearched);
			//gets the number of characters in the text - the index of the string to be searched and returns the smallest value
            int maxLength = Math.Min(obj.Length-(ix+3), 13);
            if (txtStatus.Text != null)
            {
                if (ix != -1)
                {
					//retrieves a substring from the specified ix+toBeSearched position with length maxLength 
                     String cod = obj.Substring(ix + toBeSearched.Length,maxLength);
					 //removes white spaces on that string
                    code = RemoveWhiteSpaces(cod);
                }
            }
            
            return code;
        }

        private void TCPClient(String ip)
        {
            //initializes a file stream to open a file from the given file path 
            FileStream fs = new FileStream(filepath, FileMode.Open);
			//get the string input and replace the "," with "." and then splits the string in reference to the ".". It finally creates an array of the string
            string ipp = (Convert.ToInt16(ip.Replace(",", ".").Split('.')[0])).ToString();
            ipp += "." + (Convert.ToInt16(ip.Replace(",", ".").Split('.')[1])).ToString();
            ipp += "." + (Convert.ToInt16(ip.Replace(",", ".").Split('.')[2])).ToString();
            ipp += "." + (Convert.ToInt16(ip.Replace(",", ".").Split('.')[3])).ToString();
			
			//converts string to an IPAddress instance
            IPAddress iPAddress = IPAddress.Parse(ipp);
			//gets the port number string from the txtPort text box and converts it to an int
            int pt1 = int.Parse(txtPort.Text);
			//increases the port number by a value of 1
            int pt = pt1 + 1;
			//initialize a new tcp client connection
            TcpClient tc = new TcpClient();
			//initializes a new network end point and connects the client to it using the obtained ip address from the ClientIP method
            IPEndPoint iPEndPoint = new IPEndPoint(iPAddress, pt);
            tc.Connect(iPEndPoint);
			//define the network stream used to send and receive data from the network 
            NetworkStream ns = tc.GetStream();
			//creates  a data packet with command '125' and the file name of the desired file to transfer
            byte[] data_toSend = CreateDataPacket(Encoding.UTF8.GetBytes("125"), Encoding.UTF8.GetBytes(filename));
			//writes data packet to the stream
            ns.Write(data_toSend, 0, data_toSend.Length);
			//flushes data from the stream
            ns.Flush();
            Boolean loop_break = false;
            while (true)
            {
				//if the byte read from the stream ==2 then read the next byte and use readstream method to read the received data
                if (ns.ReadByte() == 2)
                {
                    byte[] cmd_buff = new byte[3];
                    ns.Read(cmd_buff, 0, cmd_buff.Length);
                    byte[] recv_data = ReadStream(ns);
					//for cmd_buff value select case
                    switch (Convert.ToInt32(Encoding.UTF8.GetString(cmd_buff)))
                    {
                        case 126:
                            long receive_file_pointer = long.Parse(Encoding.UTF8.GetString(recv_data));
							//checks if receive_file_pointer equals the length of the file stream
                            if (receive_file_pointer != fs.Length)
                            {
								//sets the received data pointer in reference to the file origin
                                fs.Seek(receive_file_pointer, SeekOrigin.Begin);
								//initializes a buffer byte of length 20000  and fills it with data from the filestream
                                int temp_buff_length = (int)(fs.Length - receive_file_pointer < 20000 ? fs.Length - receive_file_pointer : 20000);
                                byte[] temp_buff = new byte[temp_buff_length];
                                fs.Read(temp_buff, 0, temp_buff.Length);
								//sends a data packet with command '127' and the buffer byte
                                byte[] data_to_send = CreateDataPacket(Encoding.UTF8.GetBytes("127"), temp_buff);
                                ns.Write(data_to_send, 0, data_to_send.Length);
                                ns.Flush();
								//calculates the progress bars value
                                pb_upload.Value = (int)Math.Ceiling((double)receive_file_pointer / (double)fs.Length * 100);
                            }
                            else
                            {
								//sends data packet with command '128 and string 'Close
                                byte[] data_to_send = CreateDataPacket(Encoding.UTF8.GetBytes("128"), Encoding.UTF8.GetBytes("Close"));
                                ns.Write(data_to_send, 0, data_to_send.Length);
                                ns.Flush();
                                fs.Close();
								//sets loop break to true
                                loop_break = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (loop_break == true)
                {
					//closes the current stream and releases any resources such as sockets or file handles associated with it
                    ns.Close();
                    break;
                }
            }
        }
		
		//the method reads incoming stream

        public byte[] ReadStream(NetworkStream ns)
        {
            byte[] data_buff = null;

            int b = 0;
            String buff_length = "";
			//reads stream bytes except from the separator byte = 4
            while ((b = ns.ReadByte()) != 4)
            {
                buff_length += (char)b;
            }
            int data_length = Convert.ToInt32(buff_length);
			//creates a new byte of length equal to the bytes read
            data_buff = new byte[data_length];
            int byte_read = 0;
            int byte_offset = 0;
			//as long as the byte_offset is smaller than the number of bytes read, reads data from the stream and places them in data_buff
            while (byte_offset < data_length)
            {
                byte_read = ns.Read(data_buff, byte_offset, data_length - byte_offset);
                byte_offset += byte_read;
            }
            return data_buff;
        }

		//the method creates outgoing packets 
        private byte[] CreateDataPacket(byte[] cmd, byte[] data)
        {
			//initializes two static value bytes
            byte[] initialize = new byte[1];
            initialize[0] = 2;
            byte[] separator = new byte[1];
            separator[0] = 4;
			//determines the byte length of data input 
            byte[] datalength = Encoding.UTF8.GetBytes(Convert.ToString(data.Length));
			//initializes a stream with memory as backing store, writes the bytes to it and returns it as an array
            MemoryStream ms = new MemoryStream();
            ms.Write(initialize, 0, initialize.Length);
            ms.Write(cmd, 0, cmd.Length);
            ms.Write(datalength, 0, datalength.Length);
            ms.Write(separator, 0, separator.Length);
            ms.Write(data, 0, data.Length);
            return ms.ToArray();
        }

        private void btnStop_Click(object sender, EventArgs e)
        {
			//clears text box
            txtStatus.Text = "";
        }


        private void btnSend_Click(object sender, EventArgs e)
        {
			//clears text box
            txtSend.Text = "";

            button2.PerformClick();

            try
            {
				// calls ClientIP() and TCPClient() methods
                string ip = ClientIP();
                TCPClient(ip);

                txtSend.Text = "File Sent Succesfully\r\n";
                txtStatus.Text = "";
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }

        }

        private void txtStatus_TextChanged(object sender, EventArgs e)
        {
            try
            {
                string ip = ClientIP();

                if (txtStatus.Text.Contains("Receive file") == true)
                {

                    button2.PerformClick();
                   

                    if (btnSend.Enabled == false)
                    {                                    
                        TCPClient(ip);

                        txtSend.Text = "File Sent Succesfully\r\n";
                        txtStatus.Text = "";
                    }
                    return;
                }


            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }


        private void checkBox2_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox2.Checked)
            {
                dateTimePicker1.Enabled = false;
                dateTimePicker2.Enabled = false;
                check1 = true;
            }
            else
            {
                dateTimePicker1.Enabled = true;
                dateTimePicker2.Enabled = true;

                check1 = false;
            }
        }

        private void checkBox3_CheckedChanged(object sender, EventArgs e)
        {
            if (checkBox3.Checked)
            {
                btnSend.Enabled = false;
            }
            else
            {
                btnSend.Enabled = true;
            }
        }
    } 
}
