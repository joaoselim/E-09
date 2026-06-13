volatile uint8_t movimento = 0;
uint8_t ultimo_movimento = 0;

/*Preciso agora adicionar a parte de comunicação UART, além dos pinos de saída que vou mudar para o PORTB, então todos os leds, buzzers, etc. irão para lá*/

// Configurações da UART
#define FOSC 16000000U                 // Frequência do clock (16 MHz)
#define BAUD 9600                      // Baud rate desejado
#define MYUBRR ((FOSC/16/BAUD)-1)


// Função para inicializar a UART
void UART_Init(unsigned int UBRR) {

    // Configura registradores de baud rate (parte alta e baixa)
    UBRR0H = (unsigned char)(UBRR >> 8);
    UBRR0L = (unsigned char)UBRR;

    // Habilita o transmissor (TX)
    UCSR0B = (1 << TXEN0);

    // Configura o formato da mensagem: 8 bits de dados, 1 stop bit, sem paridade
    UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
}

void UART_Transmit(uint8_t data)
{
    while(!(UCSR0A & (1 << UDRE0)));

    UDR0 = data;
}


// PULO
ISR(INT0_vect){
  if(!(PIND & (1<<PD2))){
    movimento |= (1<<2);
    PORTB |= (1<<PB4);
  }
  else {
    movimento &= ~(1<<2);
    PORTB &= ~(1<<PB4);
  }
}

// INTERAGIR
ISR(INT1_vect){
  if(!(PIND & 0b00001000))
        movimento |= (1<<3);
    else
        movimento &= ~(1<<3);
}

// INVENTÁRIO
ISR(PCINT2_vect){
   if(!(PIND & 0b00010000)){
      movimento |= (1 << 4);
    }
    else{
      movimento &= ~(1 << 4);
    }
} 

// MOVIMENTOS ESQUERDA E DIREITA
ISR(TIMER0_COMPA_vect)
{
    if(!(PIND & 0b00100000))
        movimento |= (1<<0);
    else
        movimento &= ~(1<<0);

    if(!(PIND & 0b01000000))
        movimento |= (1<<1);
    else
        movimento &= ~(1<<1);
}

int main()
{
  DDRD = 0b00000010; //PD2 (PULO) E PD3 (INTERAÇÃO) SÃO INT EXTERNA, PD4 (INVENTÁRIO) É PCINT, PD5 E PD6 SERÃO VISTO ATRAVÉS DE TIMER
  PORTD = 0b01111100;                   //OS 5 BOTÕES ESTÃO EM PULL-UP

  DDRB = 0b11111111; //PB2 LED, PB4 BUZZER
  PORTB = 0b00000010;

  //PARTE DO TIMER
  TCCR0A |= (1 << WGM01);               //MODO DE OPERAÇÃO CTC(POR COMPARAÇÃO)
  TCCR0B |= (1 << CS01) | (1 << CS00);  //PRESCALER = 64, FREQUÊNCIA DE 250kHz E PERÍODO DE 4 micro-s
  OCR0A = 249;                          //CONTA DE 0 ATÉ 249 (250 TICKS), A CADA 1 ms OCORRE INTERRUPÇÃO

  TIMSK0 |= (1 << OCIE0A);              // Interrupção Compare A 

  //PARTE DE INTERRUPÇÃO
  //INT0 E INT1 
  EIMSK |= (1 << INT0);                 //HABILITANDO INT0
  EIMSK |= (1 << INT1);                 //HABILITANDO INT1

  EICRA &= ~(1<<ISC01);
  EICRA |=  (1<<ISC00);
  EICRA &= ~(1<<ISC11);
  EICRA |=  (1<<ISC10);

  //PCINT
  PCICR |= (1 << PCIE2);                //HABILITANDO PCINT NO PORTD
  PCMSK2 |= (1 << PD4);                 //ATIVANDO A INTERRUPÇÃO NO PD4

  UART_Init(MYUBRR);

  sei(); 

  while(1)
  {
    if(movimento != ultimo_movimento)
    {
        UART_Transmit(movimento);
        
        ultimo_movimento = movimento;
    }

    if(movimento)
      PORTB |= (1 << PB2);
    else
      PORTB &= ~(1 << PB2);
  } 
}