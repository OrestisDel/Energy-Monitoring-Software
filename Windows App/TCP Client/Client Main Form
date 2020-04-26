using System;
using System.Text;
using System.Windows.Forms;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.IO;

namespace TCP_Client
{
    public partial class Client : Form
    {
        private Socket clientSocket;
        private byte[] buffer;

        private TcpListener listen;

        public Client()
        {
            InitializeComponent();         
        }


		//gets local host IP Address from the DNS server 
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

		//if btnConnect is clicked 
        private void btnConnect_Click(object sender, EventArgs e)
        {
            txtBox.Text = "";
            txtFT.Text = "";
			//if entered string is a valid IP Address format proceed
            if (System.Uri.CheckHostName(txtIP.Text) == UriHostNameType.IPv4)
            {
				//convert ip and port number strings to valid formats
                IPAddress ip = IPAddress.Parse(txtIP.Text);
                int pt = int.Parse(txtPort.Text);
                try
                {
					//initializes a new socket instance using the specified address family (IPv4), socket and protocol type
                    clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
					//begins an asynchronous request for a remote host with the specified IP and port number
                    clientSocket.BeginConnect(new IPEndPoint(ip, pt), new AsyncCallback(ConnectCallback), null);                  
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
                }

            }
            else
            {
                MessageBox.Show("Please provide a valid Server IP", Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }


        }


        private void ConnectCallback(IAsyncResult ar)
        {
            try
            {
				//asynchronously sends a specified buffer containing client name and ip address to the connected socket
                byte[] buffer = Encoding.ASCII.GetBytes( " TCP Connection established with " + txtUser.Text + "\r\n\t IP: " + GetLocalIPAddress() +"\r\n");
                clientSocket.BeginSend(buffer, 0, buffer.Length, SocketFlags.None, new AsyncCallback(SendCallback), null);
                UpdateControlStates(true);
				//calls the receive loop listening for data send back from the server
                ReceiveLoop();
				//ends a pending asynchronous read
                clientSocket.EndReceive(ar);
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
				//ends a pending asynchronous send
                clientSocket.EndSend(ar);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void ReceiveLoop()
        {
            try
            {
                while (true)
                {
					//creates a buffer of length 1024 bits and puts the received data in it
                    byte[] ReceiveBuf = new byte[1024];
                    int rec = clientSocket.Receive(ReceiveBuf);
                    byte[] data = new byte[rec];
					//copies the buffer to the new data byte array of length equal to the buffer length
                    Array.Copy(ReceiveBuf, data, rec);
					//converts the array to a string and sends it to appendtotextbox method
                    AppendToTextBox(Encoding.ASCII.GetString(data));
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void UpdateControlStates(bool toggle)
        {
			//the btnFileTransf button is enabled
            MethodInvoker invoker = new MethodInvoker(delegate ()
            {
                btnFileTransf.Enabled = toggle;
            });
            this.Invoke(invoker);
        }

        private void AppendToTextBox(string text)
        {
			//displays input string to txtBox text box
            MethodInvoker invoker = new MethodInvoker(delegate ()
            {
                txtBox.Text += "\n" + "Server: " + text + "\r\n" + "\r\n";
            });
            this.Invoke(invoker);
        }
		
        private void txtIP_TextChanged(object sender, EventArgs e)
        {
            btnConnect.Enabled = true;
        }

        private void btnFileTransf_Click(object sender, EventArgs e)
        {
            txtFT.Text = "";
            IPAddress ip = IPAddress.Parse(txtIP.Text);
            int pt = int.Parse(txtPort.Text);

            try
            {
				//if the specified file in the directory exists then deletes it
                if (File.Exists(@"C:\Users\Orestis\source\repos\login\TCP Client\bin\DebugExprt_txt.txt"))
                {
                    File.Delete(@"C:\Users\Orestis\source\repos\login\TCP Client\bin\DebugExprt_txt.txt");
                }
	
				//initializes a new socket instance using the specified address family (IPv4), socket and protocol type
                clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
				//begins an asynchronous request for a remote host with the specified IP and port number
                clientSocket.BeginConnect(new IPEndPoint(ip, pt), new AsyncCallback(TransferCallback), null);
                
				//creates a new thread from the TCPServer class and call Start Server method
                TCPServer obj_server = new TCPServer();
                Thread obj_thread = new Thread(obj_server.StartServer);
				//causes the operating system to change the instance of the specified Thread to ThreadState.Running
                obj_thread.Start();
                
                txtFT.Text = "File Received Succesfully\r\n Press 'Open' to view file...\r\n";
             
                btnOpen.Enabled = true;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }
		
       private void TransferCallback(IAsyncResult ar)
        {

            try
            {
				//asynchronously sends a specified buffer to the connected socket
                byte[] buffer = Encoding.ASCII.GetBytes(txtUser.Text + " is ready to Receive file");
                clientSocket.BeginSend(buffer, 0, buffer.Length, SocketFlags.None, new AsyncCallback(SendCallback), null);
                clientSocket.EndReceive(ar);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, Application.ProductName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }		

        private void button1_Click(object sender, EventArgs e)
        {
            if (checkBox1.Checked)
            {
				//ends current process
                System.Diagnostics.Process.GetCurrentProcess().Kill();
                Application.Exit();
            }
            else
            {
				//returns to client login form
                this.Hide();
                ClientLogin cl = new ClientLogin();
                cl.Show();
            }      
        }
		
        private void btnOpen_Click(object sender, EventArgs e)
        {
			//shows the ClientData Form
            ClientData cd = new ClientData();
            cd.Show();
        }		
    }
	
    class TCPServer
    {
        TcpListener obj_server;

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

        public TCPServer()
        {
			//gets txtIP and txtPort strings from the public partial class Client : Form
            string ip1 = ((TextBox)Client.ActiveForm.Controls.Find("txtIP", true)[0]).Text;
            IPAddress ip = IPAddress.Parse(GetLocalIPAddress());
            string port = ((TextBox)Client.ActiveForm.Controls.Find("txtPort", true)[0]).Text;

            int pt1 = int.Parse(port);
			//increases the port number by int 1
            int pt = pt1 + 1;
			//listens for connection request on the specified ip and port number
            obj_server = new TcpListener(ip, pt);
        }
    
        public void StartServer()
        {
            obj_server.Start();
            while (true)
            {
				//accepts a pending connection request
                TcpClient tc = obj_server.AcceptTcpClient();
				//calls a new thread from the SocketHandler class 
                SocketHandler obj_handler = new SocketHandler(tc);
                Thread obj_thread = new Thread(obj_handler.ProcessSocketRequest);
                obj_thread.Start();         
            }
        }

        class SocketHandler
        {
            NetworkStream ns;
            public SocketHandler(TcpClient tc)
            {
				//returns the NetworkStream used to send and receive data
                ns = tc.GetStream();
            }
            public void ProcessSocketRequest()
            {
				//provides a stream for a file
                FileStream fs = null;
                long current_file_pointer = 0;
                Boolean loop_break = false;
                while (true)
                {
					//if byte read from the stream =2 proceeds
                    if (ns.ReadByte() == 2)
                    {
						//initializes a byte of length 3 and reads data from the network 
                        byte[] cmd_buff = new byte[3];
                        ns.Read(cmd_buff, 0, cmd_buff.Length);
						//reads data from the stream
                        byte[] recv_data = ReadStream();
                        switch (Convert.ToInt32(Encoding.UTF8.GetString(cmd_buff)))
                        {
                            case 125:
                                {
									//creates a new file on the specified path using the received file name
                                    fs = new FileStream(@"C:\Users\Orestis\source\repos\login\TCP Client\bin\Debug" + Encoding.UTF8.GetString(recv_data), FileMode.CreateNew);   
									//sends a data packet with command '126' and the current file pointer value at 0
                                    byte[] data_to_send = CreateDataPacket(Encoding.UTF8.GetBytes("126"), Encoding.UTF8.GetBytes(Convert.ToString(current_file_pointer)));
                                    ns.Write(data_to_send, 0, data_to_send.Length);
                                    ns.Flush();
                                }
                                break;
                            case 127:
                                {
									//finds the current file pointer value
                                    fs.Seek(current_file_pointer, SeekOrigin.Begin); 
									//writes received data in the file
                                    fs.Write(recv_data, 0, recv_data.Length);
									//positions current file pointer at value equal to the current length of the file 
                                    current_file_pointer = fs.Position;
									//sends a data packet with command '127' and the current file pointer value
                                    byte[] data_to_send = CreateDataPacket(Encoding.UTF8.GetBytes("126"), Encoding.UTF8.GetBytes(Convert.ToString(current_file_pointer)));
                                    ns.Write(data_to_send, 0, data_to_send.Length);
                                    ns.Flush();
                                }
                                    break;
                            case 128:
                                {
									//if command '128' is received, it closes the file and the network stream
                                    fs.Close();
                                    loop_break = true;
                                }
                                break;                               
                            default:
                                break;
                        }
                    }
                    if(loop_break == true)
                    {
                        ns.Close();                     
                        break;
                    }
                }
            }

            public byte[] ReadStream()
            {
                byte[] data_buff = null;

                int b = 0;
                String buff_length = "";
				//read the number of bytes that are not equal to 4
                while ((b = ns.ReadByte()) != 4)
                {
                    buff_length += (char)b;
                }
                int data_length = Convert.ToInt32(buff_length);
				//creates a byte with length equal to the read bytes
                data_buff = new byte[data_length];
                int byte_read = 0;
                int byte_offset = 0;
				//for byte_offset less than the length of the read bytes
                while (byte_offset < data_length)
                {
					//reads the network stream
                    byte_read = ns.Read(data_buff, byte_offset, data_length - byte_offset);
                    byte_offset += byte_read;
                }
                return data_buff;
            }
			
            private byte[] CreateDataPacket(byte[] cmd, byte[] data)
            {
                byte[] initialize = new byte[1];
                initialize[0] = 2;
                byte[] separator = new byte[1];
                separator[0] = 4;
                byte[] datalength = Encoding.UTF8.GetBytes(Convert.ToString(data.Length));
				//creates a stream backed from memory and writes the created bytes in it in an array format
                MemoryStream ms = new MemoryStream();
                ms.Write(initialize, 0, initialize.Length);
                ms.Write(cmd, 0, cmd.Length);
                ms.Write(datalength, 0, datalength.Length);
                ms.Write(separator, 0, separator.Length);
                ms.Write(data, 0, data.Length);
                return ms.ToArray();
            }
        }
    }
}
