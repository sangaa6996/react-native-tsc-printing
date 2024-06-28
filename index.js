import { NativeModules } from 'react-native';

const { TscPrinting } = NativeModules;

export default class TscPrint{
    constructor(){
        this.PrinterInstance = TscPrinting
        this.listCommand = []
    }

    setConfig = (config) =>{
        this.config = config
    }

    getInstance = ()=> this.PrinterInstance
    
    executePrint = ()=>{
        return new Promise((resolve, reject)=>{
            this.PrinterInstance.executePrint(this.config , this.listCommand).then(()=>{
                this.listCommand = [] ;
                resolve()
            }).catch(e=>reject(e));
        })
    }

    //Arguments: x Specify x-coordinate of upper left corner (in dots)
    //           y Specify y-coordinate of upper left corner (in dots)
    //           x_end Specify x-coordinate of lower right corner (in dots)
    //           y_end Specify y-coordinate of lower right corner (in dots)
    //           line thickness Line thickness (in dots)
    //           radius Optional. Specify the round corner. Default is 0
    printBox = (x,y,x_end , y_end ,line = 6 , radius = 10)=>{
        const {scale , addToList} = this
        addToList(`BOX ${scale(x)} , ${scale(y)} , ${scale(x_end)} , ${scale(y_end)} , ${scale(line)}, ${scale(radius)}`)
    }

    print=  () => {
        this.addToList(`PRINT 1,1`);
    }

    /**
     * Arguments : 
     * x
     * y
     * height
     * content
     * humanCode : 0: not readable, 1: human readable aligns to left, 2: human readable aligns to center, 3: human readable aligns to right
     * rotation : 0 : No rotation, 90 : Rotate 90 degrees clockwise, 180 : Rotate 180 degrees clockwise, 270 : Rotate 270 degrees clockwise
     * narrow : Width of narrow element (in dots)
     * wide : Width of wide element (in dots)
     * alignment: Specify the alignment of barcode , 0 : default (Left) , 1 : Left , 2 : Center , 3 : Right
     */
    printBarcode = (x, y , height , content, humanCode = 2,narrow = 2 , wide = 2, type = '128',  rotation = 0  ,alignment = 0)=>{
        const {scale , addToList} = this
        addToList(`BARCODE ${scale(x)},${scale(y)},"${type}" , ${scale(height)} , ${humanCode} , ${rotation} , ${narrow} , ${wide} , ${alignment},"${content}"`);
    }

     /**
     * Arguments : 
     * x
     * y
     * size : 1~10
     * content
     * rotation : 0 : No rotation, 90 : Rotate 90 degrees clockwise, 180 : Rotate 180 degrees clockwise, 270 : Rotate 270 degrees clockwise
     */
    printQRCode = (x , y , size , content  ,rotation = 0 )=>{
        const {scale , addToList} = this
        addToList(`QRCODE ${scale(x)},${scale(y)},H,${scale(size)},A,${rotation},M2,"${content}"`)
    }

    /*
        Arguments : 
        x : x Postition,
        y : y position ,
        width : text 's width multiplier, 
        height : text 's height multiplier
        isBold, 
        rotation : 0 : No rotatio ,90: degrees, in clockwise direction ,180 : degrees, in clockwise direction ,270 : degrees, in clockwise direction
        alignment : 0 : Default (Left) ,1 : Left ,2 : Center ,3 : Right
    */
    printText = (x , y, width , height, text, isBold = false,rotation = 0 ,alignment = 0) =>{
        var font = 'TAHOMA.TTF'
        if(isBold)  font = 'TAHOMAB.TTF'
        const {scale , addToList} = this
        addToList(`TEXT ${scale(x)} , ${scale(y)} , "${font}" , ${rotation}, ${scale(width)} , ${scale(height)}, ${alignment} , "${text}"`);
    }

    /*
        Arguments : 
        x : x Postition
        y : y position ,
        widthBlock : width of block
        heightBlock :  height of block
        isBold, 
        rotation : 0 : No rotatio ,90: degrees, in clockwise direction ,180 : degrees, in clockwise direction ,270 : degrees, in clockwise direction
        alignment : 0 : Default (Left) ,1 : Left ,2 : Center ,3 : Right,
        widthText , 
        heightText, 
        spacing: 
    */
    printBlock = (x , y , widthBlock, heightBlock ,  content  ,  widthText , heightText,isBold = false , spacing = 4 , alignment = 0  , rotation = 0 )=>{
        var font = 'TAHOMA.TTF'
        if(isBold)  font = 'TAHOMAB.TTF'
        const {scale , addToList} = this
        addToList(`BLOCK ${scale(x)},${scale(y)},${scale(widthBlock)},${scale(heightBlock)},"${font}",${rotation},${scale(widthText)},${scale(heightText)},${scale(spacing)},${alignment},1,"${content}"`);
    }

     /*
        Arguments : 
        x1, y1 : Coordinate of first point 
        x2, y2 : Coordinate of second point
        thickness 
    */
    printDiagonal = (x1 , y1, x2, y2 , thickness = 4) =>{
        const {scale , addToList} = this
        addToList(`DIAGONAL ${scale(x1)},${scale(y1)},${scale(x2)},${scale(y2)},${thickness}`)
    }

    printBMP = (x,y , logoName)=>{
        const {scale , addToList} = this
        addToList(`PUTBMP ${scale(x)},${scale(y)},"${logoName}"`)
    }

    sendRaw = text=>{
        this.addToList(text);
    }

    addToList = (string)=>{
        this.listCommand.push(string + '\n');
    }

    scale = (number)=> {
        return number
    }
}

